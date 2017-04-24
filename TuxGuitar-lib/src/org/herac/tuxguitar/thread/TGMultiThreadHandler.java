package org.herac.tuxguitar.thread;

import org.herac.tuxguitar.util.TGException;

public class TGMultiThreadHandler implements TGThreadHandler {
	
	public TGMultiThreadHandler() {
		super();
	}
	
	public Object getThreadId() {
		return Thread.currentThread().getId();
	}
	
	public void yield() {
		Thread.yield();
	}
	
	public void start(Runnable runnable) {
		new Thread(runnable).start();
	}
	
	public void loop(TGThreadLoop loop) {
		try {
			Object mutex = new Object();
			Long timeout = null;
			while(!TGThreadLoop.BREAK.equals((timeout = loop.process()))) {
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
}
