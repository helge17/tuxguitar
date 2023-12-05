package org.herac.tuxguitar.android.synchronizer;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGRunnable implements Runnable {

	private TGContext context;
	private Runnable runnable;

	public TGRunnable(TGContext context, Runnable runnable) {
		this.context = context;
		this.runnable = runnable;
	}

	public void run() {
		try {
			this.runnable.run();
		} catch (Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(new TGException(throwable));
		}
	}
}
