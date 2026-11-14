package app.tuxguitar.vst3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

/**
 * VST3 hosting for one TuxGuitar: where the host process is, the VST3 folders, the plugins a scan of them found, and the
 * host processes plugins run in: one per plugin opened, so a plugin that crashes or hangs takes only its own process
 * down (each stop is reported), and one for a scan, ended when it is done.
 */
public final class TGVst3Hosting {

	private File executable;
	private List<File> folders = Collections.emptyList();
	private List<TGVst3Plugin> plugins = Collections.emptyList();
	private final List<String> stops = new ArrayList<String>();
	// the host processes of the plugins opened, until each is closed or found stopped
	private final List<TGVst3Host> hosts = new ArrayList<TGVst3Host>();

	public static TGVst3Hosting getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGVst3Hosting.class.getName(), new TGSingletonFactory<TGVst3Hosting>() {
			public TGVst3Hosting createInstance(TGContext context) {
				return new TGVst3Hosting();
			}
		});
	}

	// the host process's executable and the VST3 folders, searched in order
	public synchronized void configure(File executable, List<File> folders) {
		this.executable = executable;
		this.folders = new ArrayList<File>(folders);
	}

	public synchronized List<File> getFolders() {
		return Collections.unmodifiableList(this.folders);
	}

	public synchronized File getExecutable() {
		return this.executable;
	}

	// the host process's executable in the folder, as it is built on the system (os: its name, as os.name says it)
	public static File executable(File folder, String os) {
		return new File(folder, "tuxguitar-vst3-host" + (os.toLowerCase(Locale.ROOT).startsWith("windows") ? ".exe" : ""));
	}

	// a new host process
	private TGVst3Host start() throws TGVst3Exception {
		if (this.executable == null) {
			throw new TGVst3Exception("no VST3 host is configured");
		}
		return TGVst3Host.start(this.executable);
	}

	// why each host process that stopped did, oldest first
	public synchronized List<String> getStops() {
		this.reap();
		return new ArrayList<String>(this.stops);
	}

	// the host processes no longer running are forgotten, the report of each that stopped (was not closed) kept
	private void reap() {
		for (Iterator<TGVst3Host> hosts = this.hosts.iterator(); hosts.hasNext();) {
			TGVst3Host host = hosts.next();
			if (!host.isRunning()) {
				this.stopped(host);
				hosts.remove();
			}
		}
	}

	private void stopped(TGVst3Host host) {
		if (!host.isClosed()) {
			this.stops.add(host.getExitReport());
		}
	}

	// the plugins of every VST3 module in the folders; a module the host refuses (not a VST3 module, or one that takes
	// the host down) is left out
	public synchronized List<TGVst3Plugin> scan() throws TGVst3Exception {
		List<TGVst3Plugin> found = new ArrayList<TGVst3Plugin>();
		TGVst3Host scanner = null;
		try {
			for (File module : TGVst3Folders.modules(this.folders)) {
				scanner = this.running(scanner);
				try {
					found.addAll(scanner.scan(module));
				} catch (TGVst3Exception refused) {
					// not a plugin TuxGuitar can use
				}
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		found.sort(Comparator.comparing(TGVst3Plugin::getName));
		this.plugins = Collections.unmodifiableList(found);
		return this.plugins;
	}

	// as the last scan found them
	public synchronized List<TGVst3Plugin> getPlugins() {
		return this.plugins;
	}

	// the plugin of the last scan with the name, or null
	public synchronized TGVst3Plugin find(String name) {
		for (TGVst3Plugin plugin : this.plugins) {
			if (plugin.getName().equals(name)) {
				return plugin;
			}
		}
		return null;
	}

	// the scanner, or a new one when there is none yet or it stopped (its stop reported)
	private TGVst3Host running(TGVst3Host scanner) throws TGVst3Exception {
		if (scanner != null && scanner.isRunning()) {
			return scanner;
		}
		if (scanner != null) {
			this.stopped(scanner);
		}
		return this.start();
	}

	// the plugin, in a host process of its own that ends when the plugin closes
	public synchronized TGVst3Instance open(TGVst3Plugin plugin, float sampleRate, int maxBlock) throws TGVst3Exception {
		this.reap();
		TGVst3Host host = this.start();
		this.hosts.add(host);
		try {
			TGVst3Instance instance = host.open(plugin, sampleRate, maxBlock);
			host.dedicate();
			return instance;
		} catch (TGVst3Exception e) {
			host.close();
			throw e;
		}
	}

	// the host processes end, and their plugins with them
	public synchronized void close() {
		this.hosts.forEach(TGVst3Host::close);
		this.hosts.clear();
	}
}
