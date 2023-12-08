package org.herac.tuxguitar.app.action.listener.lock;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionEvent;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.listener.thread.TGSyncThreadAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;

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