package org.herac.tuxguitar.android.drawer;

import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.android.navigation.TGNavigationEvent;
import org.herac.tuxguitar.android.navigation.TGNavigationFragment;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGDrawerEventListener implements TGEventListener {
	
	private TGDrawerManager drawerManager;
	
	public TGDrawerEventListener(TGDrawerManager drawerManager) {
		this.drawerManager = drawerManager;
	}
	
	public void closeDrawer() {
		this.drawerManager.closeDrawer();
	}
	
	public void processNavigationEvent(TGEvent event) {
		this.drawerManager.onOpenFragment(((TGNavigationFragment) event.getAttribute(TGNavigationEvent.PROPERTY_LOADED_FRAGMENT)).getController());
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.drawerManager.findContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				if( TGNavigationEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					TGDrawerEventListener.this.processNavigationEvent(event);
				}
				if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					TGDrawerEventListener.this.closeDrawer();
				}
			}
		});
	}
}
