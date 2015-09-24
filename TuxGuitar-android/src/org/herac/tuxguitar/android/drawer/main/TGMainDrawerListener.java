package org.herac.tuxguitar.android.drawer.main;

import org.herac.tuxguitar.android.editor.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGMainDrawerListener implements TGEventListener {
	
	private TGMainDrawer mainDrawer;
	
	public TGMainDrawerListener(TGMainDrawer mainDrawer) {
		this.mainDrawer = mainDrawer;
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.mainDrawer.updateTransport();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			TGSynchronizer.getInstance(this.mainDrawer.findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					TGMainDrawerListener.this.processUpdateEvent(event);
				}
			});
		}
	}
}
