package app.tuxguitar.android.action.listener.thread;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

import android.os.Looper;

public abstract class TGSyncThreadAction {

	private TGContext context;

	public TGSyncThreadAction(TGContext context) {
		this.context = context;
	}

	public TGContext getContext() {
		return this.context;
	}

	public boolean isUiThread() {
		return (Looper.myLooper() == Looper.getMainLooper());
	}

	public void runInUiThread(final String id, final TGActionContext context) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				executeInterceptedAction(id, context);
			}
		});
	}

	public void executeInterceptedAction(String actionId, TGActionContext context) {
		try {
			TGActionManager tgActionManager = TGActionManager.getInstance(TGSyncThreadAction.this.context);
			tgActionManager.execute(actionId, context);
		} catch (TGActionException e) {
			e.printStackTrace();
		}
	}
}
