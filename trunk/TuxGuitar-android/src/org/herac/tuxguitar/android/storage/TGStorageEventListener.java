package org.herac.tuxguitar.android.storage;

import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;

public class TGStorageEventListener implements TGEventListener {

	private static final String[] UPDATABLE_ACTION_IDS = {
		TGLoadSongAction.NAME,
		TGWriteSongAction.NAME
	};

	private TGContext context;

	public TGStorageEventListener(TGContext context){
		this.context = context;
	}
	
	public boolean isUpdatableAction(String actionId) {
		for(String updatableId : UPDATABLE_ACTION_IDS) {
			if( updatableId.equals(actionId) ) {
				return true;
			}
		}
		return false;
	}
	
	public void checkForUpdateSession(TGEvent event) {
		String actionId = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		
		if( this.isUpdatableAction(actionId) ) {
			TGStorageManager.getInstance(this.context).updateSession((TGAbstractContext) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT));
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.checkForUpdateSession(event);
		}
	}
}
