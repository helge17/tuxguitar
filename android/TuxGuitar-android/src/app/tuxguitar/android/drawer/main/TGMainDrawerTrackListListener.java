package app.tuxguitar.android.drawer.main;

import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;

public class TGMainDrawerTrackListListener implements TGEventListener {

	private TGMainDrawerTrackListAdapter adapter;

	public TGMainDrawerTrackListListener(TGMainDrawerTrackListAdapter adapter) {
		this.adapter = adapter;
	}

	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.adapter.processUpdateSelection();
		}if( type == TGUpdateEvent.SONG_UPDATED ){
			this.adapter.processUpdateTracks();
		}else if( type == TGUpdateEvent.SONG_LOADED ){
			this.adapter.processUpdateTracks();
		}
	}

	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}
