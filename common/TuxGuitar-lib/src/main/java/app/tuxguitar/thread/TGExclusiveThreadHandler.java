package app.tuxguitar.thread;

import app.tuxguitar.util.TGException;

public class TGExclusiveThreadHandler implements TGThreadHandler {

	public TGExclusiveThreadHandler() {
		super();
	}

	public void start(TGThreadPriority priority, Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setPriority(priority == TGThreadPriority.HIGH ? Thread.MAX_PRIORITY : Thread.NORM_PRIORITY);
		thread.start();
	}

	public void loop(final TGThreadLoop loop) {
		Object mutex = new Object();

		Long timeout = loop.process();
		while(!TGThreadLoop.BREAK.equals(timeout)) {
			pause(mutex, timeout);
			timeout = loop.process();
		}
	}

	// waits the given milliseconds, if any
	private void pause(Object mutex, Long timeout) {
		try {
			if( timeout != null && timeout > 0 ) {
				synchronized(mutex) {
					mutex.wait(timeout);
				}
			}
		} catch (InterruptedException e) {
			throw new TGException(e.getMessage(), e);
		}
	}

	public void yield() {
		Thread.yield();
	}

	public Object getThreadId() {
		return Thread.currentThread().getId();
	}

	public void dispose() {
		// nothing todo
	}
}

