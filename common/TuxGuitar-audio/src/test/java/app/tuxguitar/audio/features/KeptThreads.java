package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import app.tuxguitar.thread.TGThreadHandler;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadPriority;

/**
 * The threads TuxGuitar starts and the loops it hands on (TGThreadManager), kept until a step runs them: the player's
 * when it plays a beat entered, and when it starts to play.
 */
final class KeptThreads implements TGThreadHandler {

	private static final int READY_SECONDS = 30;

	private final List<Runnable> started = new ArrayList<Runnable>();
	private final List<TGThreadLoop> loops = new ArrayList<TGThreadLoop>();

	public synchronized void start(TGThreadPriority priority, Runnable runnable) {
		this.started.add(runnable);
	}

	public synchronized void loop(TGThreadLoop loop) {
		this.loops.add(loop);
	}

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

	// the player's start, here: what it started, then the loop that handed on (it waits for the synthesizer to be ready,
	// then starts the sequencer) turned until it breaks, for READY_SECONDS at most; the play loop it hands on is the
	// caller's to turn
	void runStartHere() {
		List<Runnable> runnables = new ArrayList<Runnable>(this.started);
		this.started.clear();
		runnables.forEach(Runnable::run);
		TGThreadLoop start = this.loops.remove(0);
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(READY_SECONDS);
		Long last = Stream.generate(start::process).filter(turn -> TGThreadLoop.BREAK.equals(turn) || System.nanoTime() > deadline).findFirst().get();
		assertEquals(TGThreadLoop.BREAK, last, "the synthesizer was not ready to play within " + READY_SECONDS + " seconds");
		this.loops.clear();
	}

	// the same, once the thread has started the player (it keeps what the player started), for READY_SECONDS at most
	void runStartHere(Thread starting) {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(READY_SECONDS);
		while (this.isEmpty() && starting.isAlive() && System.nanoTime() < deadline) {
			Thread.onSpinWait();
		}
		this.runStartHere();
	}

	private synchronized boolean isEmpty() {
		return this.started.isEmpty();
	}
}
