package org.herac.tuxguitar.gui.actions;

public class ActionLock {
	
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
				synchronized( ActionLock.class ) {
					ActionLock.class.wait(1);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
