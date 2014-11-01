package org.herac.tuxguitar.util;

public class TGLock {
	
	private Object lock;
	private Thread lockThread;
	
	public TGLock(){
		this.lock = new Object();
		this.lockThread = null;
	}
	
	public void lock() {
		Thread thread = Thread.currentThread();
		
		boolean lockSuccess = false;
		
		synchronized( this.lock ){
			if( ( lockSuccess = !this.isLocked( thread ) ) ){
				this.lockThread = thread;
			}
		}
		
		if( !lockSuccess ){
			while( isLocked(thread) ){
				Thread.yield();
			}
			this.lock();
		}
	}
	
	public void unlock() {
		synchronized( this.lock ){
			this.lockThread = null;
		}
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
	
	public boolean isLocked(Thread thread) {
		synchronized( this.lock ){
			return (this.lockThread != null && this.lockThread != thread);
		}
	}
	
	public boolean isLocked() {
		return isLocked( Thread.currentThread() );
	}
}
