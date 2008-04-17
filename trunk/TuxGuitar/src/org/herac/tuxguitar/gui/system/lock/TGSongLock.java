package org.herac.tuxguitar.gui.system.lock;

import org.herac.tuxguitar.util.TGLock;

public class TGSongLock {

	private static TGLock lock = new TGLock();
	
	public static void lock(){
		lock.lock();
	}

	public static void unlock(){
		lock.unlock();
	}
	
	public static boolean isLocked(){
		return lock.isLocked();
	}
	
    public static void waitFor(){
    	lock.waitFor();
    }
	
}
