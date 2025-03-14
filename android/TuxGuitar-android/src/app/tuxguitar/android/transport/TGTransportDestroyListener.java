package app.tuxguitar.android.transport;

import app.tuxguitar.editor.event.TGDestroyEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;

public class TGTransportDestroyListener implements TGEventListener{

	private TGTransportAdapter adapter;

	public TGTransportDestroyListener(TGTransportAdapter adapter){
		this.adapter = adapter;
	}

	public void processEvent(TGEvent event) {
		if( TGDestroyEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.adapter.destroy();
		}
	}
}
