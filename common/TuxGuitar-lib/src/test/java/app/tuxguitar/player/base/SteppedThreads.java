package app.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.thread.TGThreadHandler;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadPriority;

/**
 * A thread handler that runs nothing by itself: it keeps what it is given to start and to loop, in order, for a test
 * to run one step at a time on its own thread.
 */
class SteppedThreads implements TGThreadHandler {

	private final List<Runnable> started = new ArrayList<Runnable>();
	private final List<TGThreadLoop> loops = new ArrayList<TGThreadLoop>();

	public void start(TGThreadPriority priority, Runnable runnable) {
		this.started.add(runnable);
	}

	public void loop(TGThreadLoop loop) {
		this.loops.add(loop);
	}

	public void yield() { }
	public void dispose() { }
	public Object getThreadId() { return null; }

	// what was started and is still to run, then what was looped
	String pending() {
		return this.started.size() + " started, " + this.loops.size() + " looped";
	}

	// runs the first thing started
	void runStarted() {
		this.started.remove(0).run();
	}

	// runs one turn of the loop handed on last, which is then dropped; returns how long it asked to wait, or BREAK
	Long runLoop() {
		return this.loops.remove(this.loops.size() - 1).process();
	}
}
