package org.herac.tuxguitar.android.drawer.main;

import org.herac.tuxguitar.android.editor.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGMainDrawerTrackListListener implements TGEventListener {
	
	private TGMainDrawerTrackListAdapter adapter;
	
	public TGMainDrawerTrackListListener(TGMainDrawerTrackListAdapter adapter) {
		this.adapter = adapter;
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.adapter.updateSelection();
		}if( type == TGUpdateEvent.SONG_UPDATED ){
			this.adapter.updateTracks();
		}else if( type == TGUpdateEvent.SONG_LOADED ){
			this.adapter.updateTracks();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			TGSynchronizer.getInstance(this.adapter.getMainDrawer().findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					TGMainDrawerTrackListListener.this.processUpdateEvent(event);
				}
			});
		}
	}
}
