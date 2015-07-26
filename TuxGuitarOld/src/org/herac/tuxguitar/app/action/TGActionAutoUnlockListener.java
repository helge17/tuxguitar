package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGActionAutoUnlockListener implements TGEventListener {
	
	private TGActionAdapterManager manager;
	
	public TGActionAutoUnlockListener(TGActionAdapterManager manager){
		this.manager = manager;
	}
	
	public void processEvent(TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			String id = (String) event.getProperty(TGActionPostExecutionEvent.PROPERTY_ACTION_ID);
			if( this.manager.getAutoUnlockActionIds().hasActionId(id) ) {
				TGActionLock.unlock();
			}
		}
	}
}
