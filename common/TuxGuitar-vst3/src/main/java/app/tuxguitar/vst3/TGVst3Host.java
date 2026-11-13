package app.tuxguitar.vst3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The process that runs VST3 plugins apart from TuxGuitar (native/host), so a plugin that crashes takes only that process
 * down. TuxGuitar talks to it through its standard input and output, which no other process can open, one request at a
 * time (TGVst3Protocol); each plugin's audio goes through a file both map (TGVst3SharedBlocks). When the process is
 * gone, every request fails, saying so, and the host reports it stopped.
 */
public final class TGVst3Host implements AutoCloseable {

	private static final String STOPPED = "the VST3 host stopped";

	private final Process process;
	private final OutputStream requests;
	private final DataInputStream replies;
	private final File shared;
	private final Map<Integer, TGVst3Instance> instances = new HashMap<Integer, TGVst3Instance>();
	private final List<String> opened = new ArrayList<String>();
	private long pid;
	private String exitReport;

	private TGVst3Host(Process process, File shared) {
		this.process = process;
		this.requests = new BufferedOutputStream(process.getOutputStream());
		this.replies = new DataInputStream(new BufferedInputStream(process.getInputStream()));
		this.shared = shared;
	}

	// the process, started and greeted; its errors go to TuxGuitar's
	public static TGVst3Host start(File executable) throws TGVst3Exception {
		try {
			Process process = new ProcessBuilder(executable.getAbsolutePath()).redirectError(Redirect.INHERIT).start();
			File shared = Files.createTempDirectory("tuxguitar-vst3-").toFile();
			shared.deleteOnExit();
			TGVst3Host host = new TGVst3Host(process, shared);
			host.hello();
			return host;
		} catch (IOException e) {
			throw new TGVst3Exception("the VST3 host did not start: " + executable, e);
		}
	}

	private void hello() throws TGVst3Exception {
		this.pid = this.call(new TGVst3Request(TGVst3Protocol.HELLO), in -> TGVst3Protocol.greeted(in.readInt(), in.readLong()));
	}

	// the host process's id, as it reported it
	public long getPid() {
		return this.pid;
	}

	public synchronized boolean isRunning() {
		return (this.getExitReport() == null);
	}

	// why the host stopped, or null while it runs
	public synchronized String getExitReport() {
		if (this.exitReport == null && !this.process.isAlive()) {
			this.exitReport = STOPPED + " (exit code " + this.process.exitValue() + ")";
		}
		return this.exitReport;
	}

	// the effects and instruments of a module (a .vst3 bundle or file); refused when it is not a VST3 module
	public List<TGVst3Plugin> scan(File module) throws TGVst3Exception {
		String path = module.getPath();
		return this.call(new TGVst3Request(TGVst3Protocol.SCAN).writeString(path), in -> {
			List<TGVst3Plugin> plugins = new ArrayList<TGVst3Plugin>();
			for (int count = in.readInt(); count > 0; count--) {
				plugins.add(new TGVst3Plugin(path, TGVst3Request.readString(in), TGVst3Request.readString(in), in.readInt() == 1));
			}
			return plugins;
		});
	}

	// the plugin, started for blocks of up to maxBlock frames at the sample rate
	public TGVst3Instance open(TGVst3Plugin plugin, float sampleRate, int maxBlock) throws TGVst3Exception {
		TGVst3SharedBlocks blocks = TGVst3SharedBlocks.create(this.shared, maxBlock);
		TGVst3Request request = new TGVst3Request(TGVst3Protocol.OPEN).writeString(plugin.getModule()).writeString(plugin.getClassId())
			.writeDouble(sampleRate).writeInt(maxBlock).writeString(blocks.getPath());
		try {
			TGVst3Instance instance = this.call(request, in -> TGVst3Instance.opened(this, blocks, in));
			this.opened.add(plugin.getName());
			return instance;
		} catch (TGVst3Exception e) {
			blocks.close();
			throw e;
		}
	}

	// the names of the plugins opened in this process, in the order they opened
	public synchronized List<String> getOpened() {
		return new ArrayList<String>(this.opened);
	}

	// how many network sockets the process listens on (-1 when the platform cannot tell)
	public int networkListeners(long processId) throws TGVst3Exception {
		return this.call(new TGVst3Request(TGVst3Protocol.LISTENERS).writeLong(processId), in -> in.readInt());
	}

	void opened(int id, TGVst3Instance instance) {
		this.instances.put(id, instance);
	}

	void closed(int id) {
		this.instances.remove(id);
	}

	// sends the request and reads its reply, then what the plugins told the host
	synchronized <T> T call(TGVst3Request request, TGVst3Request.Reply<T> reply) throws TGVst3Exception {
		if (!this.isRunning()) {
			throw new TGVst3Exception(this.exitReport);
		}
		try {
			request.writeTo(this.requests);
			this.requests.flush();
			return this.answer(reply);
		} catch (IOException e) {
			throw this.stopped(e);
		}
	}

	private <T> T answer(TGVst3Request.Reply<T> reply) throws IOException, TGVst3Exception {
		if (this.replies.readInt() != 0) {
			String reason = TGVst3Request.readString(this.replies);
			this.readNotices();
			throw new TGVst3Exception(reason);
		}
		T value = reply.read(this.replies);
		this.readNotices();
		return value;
	}

	private void readNotices() throws IOException {
		TGVst3Notice.Kind[] kinds = TGVst3Notice.Kind.values();
		for (int count = this.replies.readInt(); count > 0; count--) {
			TGVst3Instance instance = this.instances.get(this.replies.readInt());
			TGVst3Notice notice = new TGVst3Notice(kinds[this.replies.readInt() - 1], this.replies.readInt(), this.replies.readDouble());
			if (instance != null) {
				instance.notice(notice);
			}
		}
	}

	// the pipe broke: the process is gone (or going: it is ended)
	private TGVst3Exception stopped(IOException cause) {
		this.process.destroyForcibly();
		try {
			this.process.waitFor(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		this.exitReport = STOPPED + (this.process.isAlive() ? "" : " (exit code " + this.process.exitValue() + ")");
		return new TGVst3Exception(this.exitReport, cause);
	}

	// the process ends when its input closes; it is ended if it has not within 5 seconds
	public synchronized void close() {
		try {
			this.requests.close();
			this.process.waitFor(5, TimeUnit.SECONDS);
		} catch (IOException e) {
			// the pipe is broken: the process is gone or is ended below
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		this.process.destroyForcibly();
		this.instances.clear();
	}
}
