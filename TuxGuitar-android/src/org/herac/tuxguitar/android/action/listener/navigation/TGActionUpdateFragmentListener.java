package org.herac.tuxguitar.android.action.listener.navigation;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.android.fragment.impl.TGBrowserFragmentController;
import org.herac.tuxguitar.android.fragment.impl.TGMainFragmentController;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGActionUpdateFragmentListener implements TGEventListener {
	
	private TGActivity activity;
	private Map<String, TGFragmentController<?>> actionMap;
	
	public TGActionUpdateFragmentListener(TGActivity activity){
		this.activity = activity;
		this.actionMap = new HashMap<String, TGFragmentController<?>>();
		this.fillActionMap();
	}
	
	public void fillActionMap() {
		this.actionMap.put(TGReadSongAction.NAME, TGMainFragmentController.getInstance(this.activity.findContext()));
		this.actionMap.put(TGWriteSongAction.NAME, TGMainFragmentController.getInstance(this.activity.findContext()));
		this.actionMap.put(TGBrowserPrepareForReadAction.NAME, TGBrowserFragmentController.getInstance(this.activity.findContext()));
		this.actionMap.put(TGBrowserPrepareForWriteAction.NAME, TGBrowserFragmentController.getInstance(this.activity.findContext()));
	}
	
	public void checkForFragmentToOpen(TGEvent event) {
		String actionId = (String) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		
		if( this.actionMap.containsKey(actionId) ) {
			this.activity.getNavigationManager().callOpenFragment(this.actionMap.get(actionId));
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			TGSynchronizer.getInstance(this.activity.findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					TGActionUpdateFragmentListener.this.checkForFragmentToOpen(event);
				}
			});
		}
	}
}
