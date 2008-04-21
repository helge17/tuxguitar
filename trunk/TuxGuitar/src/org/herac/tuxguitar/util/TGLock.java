package org.herac.tuxguitar.util;



public class TGLock {

	private Thread lock;
	/*
	private int waitCount = 0;
	
	private long currentId;
	private long nextId;

	public void lock(){
		try {
			Thread thread = Thread.currentThread();

			// Already locked
			if( this.lock == thread){
				return;
			}
			
			long lockId = this.nextId ++;
			while( lockId != this.currentId && thread != this.lock){
				Thread.sleep(1);
			}

			while( isLocked(thread) ){
				Thread.sleep(1);
			}
			this.lock = thread;
			this.currentId ++ ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void unlock(){
		this.lock = null;
	}
	*/
	
	public void lock(){
		try {
			Thread thread = Thread.currentThread();
			if( this.lock != thread ){
				synchronized( this ){
					while( isLocked(thread) ){
						this.wait();
					}
					this.lock = thread;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void unlock(){
		synchronized( this ){
			this.lock = null;
			this.notifyAll();
		}
	}
	
	public boolean isLocked(Thread thread){
		return (this.lock != null && this.lock != thread);
	}
	
	public boolean isLocked(){
		return isLocked( Thread.currentThread() );
	}
}
