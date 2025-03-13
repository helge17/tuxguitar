package app.tuxguitar.android.view.browser;

import app.tuxguitar.action.TGActionErrorEvent;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.android.action.TGActionAsyncProcessFinishEvent;
import app.tuxguitar.android.action.TGActionAsyncProcessErrorEvent;
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import app.tuxguitar.android.browser.model.TGBrowserException;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer;

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
