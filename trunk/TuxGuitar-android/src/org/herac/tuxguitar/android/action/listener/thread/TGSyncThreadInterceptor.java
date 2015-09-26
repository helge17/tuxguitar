package org.herac.tuxguitar.android.action.listener.thread;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

import android.os.Looper;

public class TGSyncThreadInterceptor implements TGActionInterceptor {
	
	private TGContext context;
	private List<String> actionIds;
	
	public TGSyncThreadInterceptor(TGContext context) {
		this.context = context;
		this.actionIds = new ArrayList<String>();
	}
	
	public boolean containsActionId(String id) {
		return this.actionIds.contains(id);
	}
	
	public void addActionId(String id) {
		this.actionIds.add(id);
	}
	
	public void removeActionId(String id) {
		this.actionIds.remove(id);
	}
	
	public boolean intercept(final String id, final TGActionContext context) throws TGActionException {
		if( this.containsActionId(id) && !this.isUiThread() ) {
			this.runInUiThread(id, context);
			
			return true;
		}
		return false;
	}
	
	private boolean isUiThread() {
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
			TGActionManager tgActionManager = TGActionManager.getInstance(TGSyncThreadInterceptor.this.context);
			tgActionManager.execute(actionId, context);
		} catch (TGActionException e) {
			e.printStackTrace();
		}
	}
}
