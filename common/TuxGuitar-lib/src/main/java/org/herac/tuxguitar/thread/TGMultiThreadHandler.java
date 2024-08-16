package org.herac.tuxguitar.thread;

import java.util.ArrayList;
import java.util.List;

public class TGMultiThreadHandler implements TGThreadHandler {
	
	private TGThreadHandler defaultHandler;
	private TGThreadHandler exclusiveHandler;
	private List<Object>  exclusiveThreads;
	
	public TGMultiThreadHandler() {
		this.defaultHandler = new TGPooledThreadHandler();
		this.exclusiveHandler = new TGExclusiveThreadHandler();
		this.exclusiveThreads = new ArrayList<Object>();
	}
	
	private Object getInternalThreadId() {
		return Thread.currentThread().getId();
	}
	
	private TGThreadHandler getTargetHandler() {
		return (this.exclusiveThreads.contains(this.getInternalThreadId()) ? this.exclusiveHandler : this.defaultHandler);
	}
	
	private void markAsExclusive() {
		Object threadId = getInternalThreadId();
		if(!this.exclusiveThreads.contains(threadId)) {
			this.exclusiveThreads.add(threadId);
		}
	}
	
	private void unmarkAsExclusive() {
		Object threadId = getInternalThreadId();
		if( this.exclusiveThreads.contains(threadId)) {
			this.exclusiveThreads.remove(threadId);
		}
	}
	
	public void start(TGThreadPriority priority, final Runnable runnable) {
		if( priority == TGThreadPriority.HIGHT ) {
			this.exclusiveHandler.start(priority, new TGExclusiveRunnable(this, runnable));
		}
		else {
			this.defaultHandler.start(priority, runnable);
		}
	}
	
	public void loop(final TGThreadLoop loop) {
		this.getTargetHandler().loop(loop);
	}
	
	public void yield() {
		this.getTargetHandler().yield();
	}
	
	public void dispose() {
		this.getTargetHandler().dispose();
	}
	
	public Object getThreadId() {
		return this.getTargetHandler().getThreadId();
	}
	
	private static class TGExclusiveRunnable implements Runnable {
		
		private TGMultiThreadHandler handler;
		private Runnable target;
		
		public TGExclusiveRunnable(TGMultiThreadHandler handler, Runnable target) {
			this.handler = handler;
			this.target = target;
		}
		
		public void run() {
			this.handler.markAsExclusive();
			this.target.run();
			this.handler.unmarkAsExclusive();
		}
	}
}