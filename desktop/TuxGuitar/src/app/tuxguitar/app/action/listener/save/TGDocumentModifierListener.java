package app.tuxguitar.app.action.listener.save;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;

public class TGDocumentModifierListener implements TGEventListener {

	private TGContext context;
	private List<String> actionIds;

	public TGDocumentModifierListener(TGContext context) {
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

	public boolean isDocumentModifierAction(String id) {
		return this.actionIds.contains(id);
	}

	public void checkForDocumentModifierAction(String actionId) {
		if( this.isDocumentModifierAction(actionId) ) {
			TGDocumentListManager.getInstance(this.context).findCurrentDocument().setUnsaved(true);
		}
	}

	public void processEvent(TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForDocumentModifierAction((String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
		}
	}
}
