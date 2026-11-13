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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The process that runs VST3 plugins apart from TuxGuitar (native/host), so a plugin that crashes takes only that process
 * down. TuxGuitar talks to it through its standard input and output, which no other process can open, one request at a
 * time (TGVst3Protocol); each plugin's audio goes through a file both map (TGVst3SharedBlocks). When the process is
 * gone, every request fails, saying so, and the host reports it stopped.
 *
 * A plugin that hangs: a block whose reply is later than LATE_MILLIS is left silent, and its reply is owed. While a
 * reply is owed no request is sent (one request at a time), so the next reply read is the owed one, never taken for
 * another's: each block until it comes is silent at once, and when it comes it is dropped. A host that has owed a reply
 * for SILENT_MILLIS is taken for stopped and ended, its process tree with it. Other requests wait up to CALL_MILLIS (a
 * plugin may take seconds to open), then the host is ended too.
 */
public final class TGVst3Host implements AutoCloseable {

	// a block's reply later than this is late: about 4 blocks of 1024 frames at 44100 Hz, well above the scheduling
	// jitter of a loaded machine
	static final long LATE_MILLIS = 100;
	// a host that has owed a reply this long is taken for stopped
	static final long SILENT_MILLIS = 2000;
	// how long a request other than a block's waits for its reply
	static final long CALL_MILLIS = 30000;

	private static final String STOPPED = "the VST3 host stopped";
	private static final String SILENT = "the VST3 host did not answer for " + (SILENT_MILLIS / 1000) + " s";

	private final Process process;
	private final OutputStream requests;
	private final DataInputStream replies;
	private final File shared;
	// the plugins by id: the reader thread looks them up for the notices of a reply
	private final Map<Integer, TGVst3Instance> instances = new ConcurrentHashMap<Integer, TGVst3Instance>();
	// reads each reply on a thread of its own, so a caller can stop waiting for one
	private final ExecutorService reader = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "tuxguitar-vst3-replies");
		thread.setDaemon(true);
		return thread;
	});
	// the reply of a block that came late, not read yet, and since when (System.nanoTime)
	private Future<? extends Answer<?>> owed;
	private long owedSince;
	private final List<String> opened = new ArrayList<String>();
	private long pid;
	private String exitReport;
	// ended by TuxGuitar while it ran
	private boolean closed;
	// it runs one plugin and ends when that plugin closes
	private boolean dedicated;

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

	// whether TuxGuitar ended it while it ran (rather than it stopping)
	public synchronized boolean isClosed() {
		return this.closed;
	}

	// from now on it ends when its last plugin closes
	synchronized void dedicate() {
		this.dedicated = true;
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

	synchronized void closed(int id) {
		this.instances.remove(id);
		if (this.dedicated && this.instances.isEmpty()) {
			this.close();
		}
	}

	// sends the request and reads its reply (after a reply still owed, if it comes in time), then what the plugins told
	// the host
	synchronized <T> T call(TGVst3Request request, TGVst3Request.Reply<T> reply) throws TGVst3Exception {
		if (this.owing(SILENT_MILLIS - this.owedFor())) {
			throw this.silent();
		}
		try {
			return this.await(this.send(request, reply), CALL_MILLIS).deliver();
		} catch (TimeoutException e) {
			throw this.silent();
		}
	}

	// a block's request, sent when no reply is owed (see isLate): false, its reply owed, when that reply is late
	synchronized <T> boolean callInTime(TGVst3Request request, TGVst3Request.Reply<T> reply) throws TGVst3Exception {
		Future<Answer<T>> answer = this.send(request, reply);
		try {
			this.await(answer, LATE_MILLIS).deliver();
			return true;
		} catch (TimeoutException e) {
			this.owed = answer;
			this.owedSince = System.nanoTime();
			return false;
		}
	}

	// whether a late reply is still owed; one that came is dropped (what the plugins told the host with it is passed
	// on); a host that has owed one for SILENT_MILLIS is ended
	synchronized boolean isLate() throws TGVst3Exception {
		return this.owing(0);
	}

	private boolean owing(long wait) throws TGVst3Exception {
		if (this.owed == null) {
			return false;
		}
		try {
			this.await(this.owed, Math.max(wait, 0)).tell();
			this.owed = null;
			return false;
		} catch (TimeoutException e) {
			if (this.owedFor() >= SILENT_MILLIS) {
				throw this.silent();
			}
			return true;
		}
	}

	// how long the owed reply has been owed, in milliseconds; 0 when none is
	private long owedFor() {
		return (this.owed != null ? TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.owedSince) : 0);
	}

	private <T> Future<Answer<T>> send(TGVst3Request request, TGVst3Request.Reply<T> reply) throws TGVst3Exception {
		if (!this.isRunning()) {
			throw new TGVst3Exception(this.exitReport);
		}
		try {
			request.writeTo(this.requests);
			this.requests.flush();
		} catch (IOException e) {
			throw this.stopped(e);
		}
		return this.reader.submit(() -> this.answer(reply));
	}

	private <A> A await(Future<A> answer, long millis) throws TGVst3Exception, TimeoutException {
		try {
			return answer.get(millis, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			throw this.failed(e.getCause());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new TGVst3Exception("interrupted while waiting for the VST3 host", e);
		}
	}

	// a reply that could not be read: refused as the host said, or the pipe broke
	private TGVst3Exception failed(Throwable cause) {
		return (cause instanceof TGVst3Exception ? (TGVst3Exception) cause : this.stopped(cause));
	}

	// on the reader thread
	private <T> Answer<T> answer(TGVst3Request.Reply<T> reply) throws IOException, TGVst3Exception {
		if (this.replies.readInt() != 0) {
			TGVst3Exception refused = new TGVst3Exception(TGVst3Request.readString(this.replies));
			return new Answer<T>(null, refused, this.readNotices());
		}
		T value = reply.read(this.replies);
		return new Answer<T>(value, null, this.readNotices());
	}

	private List<Runnable> readNotices() throws IOException {
		List<Runnable> notices = new ArrayList<Runnable>();
		TGVst3Notice.Kind[] kinds = TGVst3Notice.Kind.values();
		for (int count = this.replies.readInt(); count > 0; count--) {
			TGVst3Instance instance = this.instances.get(this.replies.readInt());
			TGVst3Notice notice = new TGVst3Notice(kinds[this.replies.readInt() - 1], this.replies.readInt(), this.replies.readDouble());
			if (instance != null) {
				notices.add(() -> instance.notice(notice));
			}
		}
		return notices;
	}

	// the pipe broke: the process is gone (or going: it is ended)
	private TGVst3Exception stopped(Throwable cause) {
		this.end();
		this.exitReport = STOPPED + (this.process.isAlive() ? "" : " (exit code " + this.process.exitValue() + ")");
		return new TGVst3Exception(this.exitReport, cause);
	}

	// a reply owed too long: the host is taken for stopped
	private TGVst3Exception silent() {
		this.end();
		this.exitReport = SILENT;
		return new TGVst3Exception(SILENT);
	}

	// the process and every process it started end; waits up to 5 seconds for it
	private void end() {
		this.owed = null;
		this.process.descendants().forEach(ProcessHandle::destroyForcibly);
		this.process.destroyForcibly();
		try {
			this.process.waitFor(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		this.reader.shutdownNow();
	}

	// the process ends when its input closes, or is ended if it has not within 5 seconds (at once while it owes a reply)
	public synchronized void close() {
		this.closed = this.closed || this.isRunning();
		try {
			if (this.owed == null) {
				this.requests.close();
				this.process.waitFor(5, TimeUnit.SECONDS);
			}
		} catch (IOException e) {
			// the pipe is broken: the process is gone or is ended below
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		this.end();
		this.instances.clear();
	}

	// a reply as the reader thread read it: the request's value or the host's refusal, and what the plugins told the
	// host, passed on by the caller's thread
	private static final class Answer<T> {
		private final T value;
		private final TGVst3Exception refused;
		private final List<Runnable> notices;

		Answer(T value, TGVst3Exception refused, List<Runnable> notices) {
			this.value = value;
			this.refused = refused;
			this.notices = notices;
		}

		void tell() {
			this.notices.forEach(Runnable::run);
		}

		T deliver() throws TGVst3Exception {
			this.tell();
			if (this.refused != null) {
				throw this.refused;
			}
			return this.value;
		}
	}
}
