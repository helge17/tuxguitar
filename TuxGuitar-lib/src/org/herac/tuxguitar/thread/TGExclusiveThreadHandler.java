package org.herac.tuxguitar.thread;

import org.herac.tuxguitar.util.TGException;

public class TGExclusiveThreadHandler implements TGThreadHandler {
	
	public TGExclusiveThreadHandler() {
		super();
	}
	
	public void start(TGThreadPriority priority, Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setPriority(priority == TGThreadPriority.HIGHT ? Thread.MAX_PRIORITY : Thread.NORM_PRIORITY);
		thread.start();
	}
	
	public void loop(final TGThreadLoop loop) {
		try {
			Object mutex = new Object();
			
			while(true) {
				Long timeout = loop.process();
				if( TGThreadLoop.BREAK.equals(timeout)) {
					return;
				}
				
				if( timeout != null && timeout > 0 ) {
					synchronized(mutex) {
						mutex.wait(timeout);
					}
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

