package org.herac.tuxguitar.android.action.listener.browser;

import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.android.action.impl.intent.TGProcessIntentAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGActionUpdateBrowserListener implements TGEventListener {
	
	private static final String[] RESETTABLE_ACTION_IDS = {
		TGNewSongAction.NAME,
		TGLoadTemplateAction.NAME,
		TGProcessIntentAction.NAME
	};
	
	private TGActivity activity;
	
	public TGActionUpdateBrowserListener(TGActivity activity){
		this.activity = activity;
	}
	
	public boolean isResettableAction(String actionId) {
		for(String resettableId : RESETTABLE_ACTION_IDS) {
			if( resettableId.equals(actionId) ) {
				return true;
			}
		}
		return false;
	}
	
	public void checkForResetSessionElement(TGEvent event) {
		String actionId = (String) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		
		if( this.isResettableAction(actionId) ) {
			TGBrowserManager.getInstance(this.activity.findContext()).getSession().setCurrentElement(null);
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			TGSynchronizer.getInstance(this.activity.findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					TGActionUpdateBrowserListener.this.checkForResetSessionElement(event);
				}
			});
		}
	}
}
