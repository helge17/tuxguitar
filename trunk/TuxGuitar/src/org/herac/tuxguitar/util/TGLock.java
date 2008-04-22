package org.herac.tuxguitar.util;

public class TGLock {
	
	private Thread lock;
	private long lockId;
	private long lockCount;
	
	public TGLock(){
		this.lockId = 0;
		this.lockCount = 0;
	}
	
	public void lock(){
		Thread thread = Thread.currentThread();
		
		// Only if it isn't already locked
		if( this.lock != thread){
			
			long lockId = this.lockId ++;
			while( lockId != this.lockCount && thread != this.lock){
				Thread.yield();
			}
			
			while( isLocked(thread) ){
				Thread.yield();
			}
			
			synchronized( this ){
				this.lock = thread;
				this.lockCount ++ ;
			}
		}
	}
	
	public void unlock(){
		synchronized( this ){
			this.lock = null;
		}
	}
	
	public boolean isLocked(Thread thread){
		synchronized( this ){
			return (this.lock != null && this.lock != thread);
		}
	}
	
	public boolean isLocked(){
		return isLocked( Thread.currentThread() );
	}
}
