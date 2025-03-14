package app.tuxguitar.app.action.listener.lock;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionErrorEvent;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.listener.thread.TGSyncThreadAction;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;

public class TGLockableActionListener extends TGSyncThreadAction implements TGActionInterceptor, TGEventListener {

	private List<String> actionIds;

	public TGLockableActionListener(TGContext context){
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

	public boolean checkForTryLock(String actionId) {
		if( this.containsActionId(actionId) && this.isUiThread()) {
			return TuxGuitar.instance().tryLock();
		}
		return true;
	}

	public void checkForLock(String actionId) {
		if( this.containsActionId(actionId) && !this.isUiThread()) {
			TuxGuitar.instance().lock();
		}
	}

	public void checkForUnlock(String actionId) {
		if( this.containsActionId(actionId) ) {
			TuxGuitar.instance().unlock();
		}
	}

	public void processEvent(TGEvent event) {
		if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForLock((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
		else if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForUnlock((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
		else if( TGActionErrorEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForUnlock((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
	}

	public boolean intercept(String id, TGActionContext context) throws TGActionException {
		if(!this.checkForTryLock(id)) {
			this.runInUiThread(id, context);

			return true;
		}
		return false;
	}
}