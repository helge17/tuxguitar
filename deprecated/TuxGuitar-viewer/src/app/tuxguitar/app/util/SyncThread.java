package app.tuxguitar.app.util;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

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