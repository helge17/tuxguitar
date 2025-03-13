package app.tuxguitar.android.action.listener.thread;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.util.TGContext;

public class TGSyncThreadInterceptor extends TGSyncThreadAction implements TGActionInterceptor {

	private List<String> actionIds;

	public TGSyncThreadInterceptor(TGContext context) {
		super(context);

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
}
