package app.tuxguitar.android.storage;

import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.editor.action.file.TGLoadSongAction;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;

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
