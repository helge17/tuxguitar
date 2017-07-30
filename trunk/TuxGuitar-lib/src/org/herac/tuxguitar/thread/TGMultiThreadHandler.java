package org.herac.tuxguitar.thread;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.util.TGException;

public class TGMultiThreadHandler implements TGThreadHandler, Runnable {
	
	private static final int WORKER_COUNT = 10;
	private Object mutex;
	private List<Runnable> queue;
	
	public TGMultiThreadHandler() {
		this.mutex = new Object();
		this.queue = new ArrayList<Runnable>();
		
		for(int i = 0 ; i < WORKER_COUNT ; i ++) {
			new Thread(this).start();
		}
	}
	
	public Object getThreadId() {
		return Thread.currentThread().getId();
	}
	
	public void yield() {
		Thread.yield();
	}
	
	public void start(Runnable runnable) {
		synchronized (this.mutex) {
			this.queue.add(runnable);
		}
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
	
	public void run() {
		while(true) {
			this.processNext();
			this.yield();
		}
	}
}

