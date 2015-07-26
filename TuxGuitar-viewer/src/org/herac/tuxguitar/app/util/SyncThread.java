package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class SyncThread extends Thread {
	
	private Runnable runnable;
	
	public SyncThread(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void run() {
		try {
			TGSynchronizer.instance().executeLater(this.runnable);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}