package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class SyncThread extends Thread {
	
	private Runnable runnable;
	
	public SyncThread(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void run() {
		try {
			TGContext context = TuxGuitar.instance().getContext();
			TGSynchronizer.getInstance(context).executeLater(this.runnable);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}