package org.herac.tuxguitar.util;

public class TGLock {

	private Thread lock;
	
	public synchronized void lock(){
		this.lock = Thread.currentThread();
	}

	public synchronized void unlock(){
		this.lock = null;
	}
	
	public synchronized boolean isLocked(){
		return (this.lock != null && this.lock != Thread.currentThread());
	}
	
    public void waitFor(){
		try {
			while(isLocked()){
				synchronized(this.lock) {
					this.lock.wait(1);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
