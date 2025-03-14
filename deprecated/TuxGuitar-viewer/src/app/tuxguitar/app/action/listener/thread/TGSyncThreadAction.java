package app.tuxguitar.app.action.listener.thread;

import javax.swing.SwingUtilities;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public abstract class TGSyncThreadAction {

	private TGContext context;

	public TGSyncThreadAction(TGContext context) {
		this.context = context;
	}

	public TGContext getContext() {
		return this.context;
	}

	public boolean isUiThread() {
		return SwingUtilities.isEventDispatchThread();
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
