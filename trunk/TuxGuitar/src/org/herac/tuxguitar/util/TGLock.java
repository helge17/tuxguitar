package org.herac.tuxguitar.util;

public class TGLock {
	
	private boolean locked;
	
	public TGLock(){
		this.locked = false;
	}
	
	public void lock(){
		this.locked = true;
	}
	
	public void unlock(){
		this.locked = false;
	}
	
	public boolean isLocked(){
		return this.locked;
	}
	
	public void waitFor(){
		try {
			while(isLocked()){
				synchronized(this) {
					this.wait(1);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
