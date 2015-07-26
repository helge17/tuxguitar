package org.herac.tuxguitar.app.action.listener.save;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGDocumentModifierListener implements TGEventListener {
	
	private List<String> actionIds;
	
	public TGDocumentModifierListener() {
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
	
	public boolean isDocumentModifierAction(String id) {
		return this.actionIds.contains(id);
	}
	
	public void checkForDocumentModifierAction(String actionId) {
		if( this.isDocumentModifierAction(actionId) ) {
			TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForDocumentModifierAction((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
	}
}
