package app.tuxguitar.audio.features;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.thread.TGThreadHandler;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadPriority;

/**
 * The threads TuxGuitar starts (TGThreadManager), kept until a step starts them: the player's when it plays a beat
 * entered. Nothing loops here: the song plays offline or on the synthesizer's clock (see LivePlay).
 */
final class KeptThreads implements TGThreadHandler {

	private final List<Runnable> started = new ArrayList<Runnable>();

	public synchronized void start(TGThreadPriority priority, Runnable runnable) {
		this.started.add(runnable);
	}

	public void loop(TGThreadLoop loop) { }
	public void yield() { }
	public void dispose() { }
	public Object getThreadId() { return null; }

	// starts each kept on a thread of its own, as the app does; the threads, to wait for
	synchronized List<Thread> run() {
		List<Thread> threads = new ArrayList<Thread>();
		this.started.forEach(runnable -> threads.add(new Thread(runnable)));
		this.started.clear();
		threads.forEach(Thread::start);
		return threads;
	}
}
