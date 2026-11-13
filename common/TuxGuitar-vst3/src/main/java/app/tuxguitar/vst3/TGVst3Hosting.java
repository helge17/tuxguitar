package app.tuxguitar.vst3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

/**
 * VST3 hosting for one TuxGuitar: where the host process is, the VST3 folders, the plugins a scan of them found, and the
 * host process plugins run in, started when first needed and started again after it stopped (each stop is reported).
 */
public final class TGVst3Hosting {

	private File executable;
	private List<File> folders = Collections.emptyList();
	private List<TGVst3Plugin> plugins = Collections.emptyList();
	private final List<String> stops = new ArrayList<String>();
	private TGVst3Host host;

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

	// the host process, started now when there is none or it stopped
	public synchronized TGVst3Host getHost() throws TGVst3Exception {
		if (this.host != null && !this.host.isRunning()) {
			this.stops.add(this.host.getExitReport());
			this.host = null;
		}
		if (this.host == null) {
			if (this.executable == null) {
				throw new TGVst3Exception("no VST3 host is configured");
			}
			this.host = TGVst3Host.start(this.executable);
		}
		return this.host;
	}

	// why each host process that stopped did, oldest first
	public synchronized List<String> getStops() {
		return new ArrayList<String>(this.stops);
	}

	// the plugins of every VST3 module in the folders; a module the host refuses (not a VST3 module, or one that takes
	// the host down) is left out
	public synchronized List<TGVst3Plugin> scan() throws TGVst3Exception {
		List<TGVst3Plugin> found = new ArrayList<TGVst3Plugin>();
		for (File module : TGVst3Folders.modules(this.folders)) {
			try {
				found.addAll(this.getHost().scan(module));
			} catch (TGVst3Exception refused) {
				// not a plugin TuxGuitar can use
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

	public synchronized TGVst3Instance open(TGVst3Plugin plugin, float sampleRate, int maxBlock) throws TGVst3Exception {
		return this.getHost().open(plugin, sampleRate, maxBlock);
	}

	// the host process ends, and its plugins with it
	public synchronized void close() {
		Optional.ofNullable(this.host).ifPresent(TGVst3Host::close);
		this.host = null;
	}
}
