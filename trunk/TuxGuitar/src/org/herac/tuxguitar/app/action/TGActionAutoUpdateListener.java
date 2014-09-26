package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGActionAutoUpdateListener implements TGEventListener {
	
	private TGActionAdapterManager manager;
	
	public TGActionAutoUpdateListener(TGActionAdapterManager manager){
		this.manager = manager;
	}
	
	public void processEvent(TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			String id = (String) event.getProperty(TGActionPostExecutionEvent.PROPERTY_ACTION_ID);
			
			TuxGuitar.instance().updateCache( this.manager.getAutoUpdateActionIds().hasActionId(id) );
		}
	}
}
