package org.herac.tuxguitar.app.action;

public class TGActionLock {
	
	private static boolean working;
	
	public synchronized static boolean isLocked(){
		return working;
	}
	
	public synchronized static void lock(){
		working = true;
	}
	
	public synchronized static void unlock(){
		working = false;
	}
	
	public synchronized static void waitFor(){
		try {
			while(isLocked()){
				synchronized( TGActionLock.class ) {
					TGActionLock.class.wait(1);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
