package org.herac.tuxguitar.android.view.browser;

import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessFinishEvent;
import org.herac.tuxguitar.android.action.TGActionAsyncProcessErrorEvent;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGBrowserEventListener implements TGEventListener {

	private static final String[] REFRESHABLE_ACTIONS = {
		TGBrowserCloseSessionAction.NAME,
		TGBrowserAddCollectionAction.NAME, 
		TGBrowserRemoveCollectionAction.NAME
	};

	private static final String[] REFRESHABLE_ASYNC_ACTIONS = {
		TGBrowserCdRootAction.NAME,
		TGBrowserCdUpAction.NAME,
		TGBrowserCdElementAction.NAME,
		TGBrowserSaveElementAction.NAME,
		TGBrowserLoadSessionAction.NAME,
	};

	private TGBrowserView browser;
	
	public TGBrowserEventListener(TGBrowserView browser) {
		this.browser = browser;
	}

	public boolean isRefreshableAction(String actionId, String[] refreshableActionIds) {
		for(String refreshableActionId : refreshableActionIds) {
			if( refreshableActionId.equals(actionId) ) {
				return true;
			}
		}
		return false;
	}

	public void processPostExecution(String id, String[] refreshableActionIds) throws TGBrowserException {
		if( TGBrowserRefreshAction.NAME.equals(id) ) {
			this.browser.refresh();
		} 
		else if(this.isRefreshableAction(id, refreshableActionIds)){
			this.browser.requestRefresh();
		}
	}
	
	public void processError(String id, String[] refreshableActionIds) throws TGBrowserException {
		if( TGBrowserRefreshAction.NAME.equals(id) || this.isRefreshableAction(id, refreshableActionIds)){
			this.browser.refresh();
		}
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.browser.findContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				try {
					String actionId = (String) event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID);
					if (TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType())) {
						processPostExecution(actionId, REFRESHABLE_ACTIONS);
					} else if (TGActionAsyncProcessFinishEvent.EVENT_TYPE.equals(event.getEventType())) {
						processPostExecution(actionId, REFRESHABLE_ASYNC_ACTIONS);
					} else if (TGActionErrorEvent.EVENT_TYPE.equals(event.getEventType())) {
						processError(actionId, REFRESHABLE_ACTIONS);
					} else if (TGActionAsyncProcessErrorEvent.EVENT_TYPE.equals(event.getEventType())) {
						processError(actionId, REFRESHABLE_ASYNC_ACTIONS);
					}
				} catch (TGBrowserException e) {
					throw new TGException(e);
				}
			}
		});
	}
}
