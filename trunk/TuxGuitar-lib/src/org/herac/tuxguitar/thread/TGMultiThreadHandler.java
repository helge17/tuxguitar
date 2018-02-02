package org.herac.tuxguitar.thread;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.util.TGException;

public class TGMultiThreadHandler implements TGThreadHandler, Runnable {
	
	private static final int WORKER_COUNT = 10;
	
	private boolean running;
	private Object mutex;
	private List<Runnable> queue;
	
	public TGMultiThreadHandler() {
		this.mutex = new Object();
		this.queue = new ArrayList<Runnable>();
		this.running = true;
		
		this.initialize();
	}
	
	public void initialize() {		
		for(int i = 0 ; i < WORKER_COUNT ; i ++) {
			new Thread(this).start();
		}
	}

	public void start(Runnable runnable) {
		synchronized (this.mutex) {
			this.queue.add(runnable);
			this.mutex.notifyAll();
		}
	}
	
	public void loop(final TGThreadLoop loop) {
		final Object mutex = new Object();
		this.start(new Runnable() {
			public void run() {
				try {
					Long timeout = loop.process();
					if(!TGThreadLoop.BREAK.equals(timeout)) {
						if( timeout != null && timeout > 0 ) {
							synchronized(mutex) {
								mutex.wait(timeout);
							}
						}
						TGMultiThreadHandler.this.start(this);
					}
				} catch (InterruptedException e) {
					throw new TGException(e.getMessage(), e);
				}
			}
		});
	}
	
	public void processNext() {
		Runnable runnable = null;
		synchronized (this.mutex) {
			if(!this.queue.isEmpty()) {
				runnable = this.queue.remove(0);
			}
		}
		if( runnable != null ) {
			runnable.run();
		}
	}
	
	public void waitForNextThread() {
		try {
			synchronized (this.mutex) {
				if( this.queue.isEmpty()) {
					this.mutex.wait();
				} else {
					Thread.yield();
				}
			}
		} catch (InterruptedException e) {
			throw new TGException(e.getMessage(), e);
		}
	}
	
	public void run() {
		while(this.running || !this.queue.isEmpty()) {
			this.processNext();
			this.waitForNextThread();
		}
	}
	
	public void yield() {
		Thread.yield();
	}
	
	public void dispose() {
		this.running = false;
	}
	
	public Object getThreadId() {
		return Thread.currentThread().getId();
	}
}

