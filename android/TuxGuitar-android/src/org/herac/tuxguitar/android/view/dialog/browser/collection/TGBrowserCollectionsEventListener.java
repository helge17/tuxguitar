package org.herac.tuxguitar.android.view.dialog.browser.collection;

import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGBrowserCollectionsEventListener implements TGEventListener {

	private static final String[] REFRESHABLE_ACTIONS = { 
		TGBrowserAddCollectionAction.NAME, 
		TGBrowserRemoveCollectionAction.NAME
	};
	
	private TGBrowserCollectionsDialog dialog;
	
	public TGBrowserCollectionsEventListener(TGBrowserCollectionsDialog dialog) {
		this.dialog = dialog;
	}
	
	public boolean isRefreshableAction(String actionId) {
		for(String refreshableActionId : REFRESHABLE_ACTIONS) {
			if( refreshableActionId.equals(actionId) ) {
				return true;
			}
		}
		return false;
	}
	
	public void processPostExecution(String id) {
		if( this.isRefreshableAction(id) ){
			this.dialog.refreshListView();
		}
	}
	
	public void processEvent(TGEvent event) {
		if (TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType())) {
			final String id = (String) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
			TGSynchronizer.getInstance(this.dialog.findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					processPostExecution(id);
				}
			});
		}
	}
}
