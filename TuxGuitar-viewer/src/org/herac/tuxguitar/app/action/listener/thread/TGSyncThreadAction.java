package org.herac.tuxguitar.app.action.listener.thread;

import javax.swing.SwingUtilities;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

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
