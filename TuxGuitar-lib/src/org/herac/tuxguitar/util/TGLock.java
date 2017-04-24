package org.herac.tuxguitar.util;

import org.herac.tuxguitar.thread.TGThreadManager;

public class TGLock {
	
	private int lockCount;
	private Object lock;
	private Object lockThread;
	private TGThreadManager threadManager;
	
	public TGLock(TGContext context){
		this.lockCount = 0;
		this.lock = new Object();
		this.lockThread = null;
		this.threadManager = TGThreadManager.getInstance(context);
	}
	
	public void lock() {
		final Object thread = this.threadManager.getThreadId();
		
		boolean lockSuccess = false;
		
		synchronized( this.lock ){
			if( ( lockSuccess = !this.isLocked( thread ) ) ){
				this.lockThread = thread;
				this.lockCount ++;
			}
		}
		
		if( !lockSuccess ){			
			while( isLocked(thread) ){
				this.threadManager.yield();
			}
			this.lock();
		}
	}
	
	public void unlock(boolean force) {
		synchronized( this.lock ){
			this.lockCount --;
			if( this.lockCount == 0 || force ) {
				this.lockCount = 0;
				this.lockThread = null;
			}
		}
	}
	
	public void unlock() {
		this.unlock(false);
	}
	
	public boolean tryLock() {
		synchronized( this.lock ){
			if( this.isLocked() ) {
				return false;
			}
			this.lock();
			
			return true;
		}
	}
	
	public boolean isLocked(Object thread) {
		synchronized( this.lock ){
			return (this.lockThread != null && !this.lockThread.equals(thread));
		}
	}
	
	public boolean isLocked() {
		return isLocked( this.threadManager.getThreadId() );
	}
}
