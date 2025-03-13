package app.tuxguitar.android.synchronizer;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.error.TGErrorManager;

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
