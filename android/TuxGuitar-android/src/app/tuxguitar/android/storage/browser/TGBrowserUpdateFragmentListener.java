package app.tuxguitar.android.storage.browser;

import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.android.fragment.TGFragmentController;
import app.tuxguitar.android.fragment.impl.TGBrowserFragmentController;
import app.tuxguitar.android.fragment.impl.TGMainFragmentController;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer;

import java.util.HashMap;
import java.util.Map;

public class TGBrowserUpdateFragmentListener implements TGEventListener {

	private TGContext context;
	private Map<String, TGFragmentController<?>> actionMap;

	public TGBrowserUpdateFragmentListener(TGContext context){
		this.context = context;
		this.actionMap = new HashMap<String, TGFragmentController<?>>();
		this.fillActionMap();
	}

	public TGActivity findActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}

	public void fillActionMap() {
		this.actionMap.put(TGReadSongAction.NAME, TGMainFragmentController.getInstance(this.context));
		this.actionMap.put(TGWriteSongAction.NAME, TGMainFragmentController.getInstance(this.context));
		this.actionMap.put(TGBrowserPrepareForReadAction.NAME, TGBrowserFragmentController.getInstance(this.context));
		this.actionMap.put(TGBrowserPrepareForWriteAction.NAME, TGBrowserFragmentController.getInstance(this.context));
	}

	public void checkForFragmentToOpen(TGEvent event) {
		String actionId = (String) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);

		if( this.actionMap.containsKey(actionId) ) {
			this.findActivity().getNavigationManager().callOpenFragment(this.actionMap.get(actionId));
		}
	}

	public void processEvent(final TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() throws TGException {
					TGBrowserUpdateFragmentListener.this.checkForFragmentToOpen(event);
				}
			});
		}
	}
}
