package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGActionAutoLockListener implements TGEventListener {
	
	private TGActionAdapterManager manager;
	
	public TGActionAutoLockListener(TGActionAdapterManager manager){
		this.manager = manager;
	}
	
	public void processEvent(TGEvent event) {
		if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			String id = (String) event.getProperty(TGActionPreExecutionEvent.PROPERTY_ACTION_ID);
			
			if( this.manager.getAutoLockActionIds().hasActionId(id) ) {
				TGActionLock.lock();
			}
		}
	}
}
