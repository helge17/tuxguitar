package org.herac.tuxguitar.android.transport;

import org.herac.tuxguitar.editor.event.TGDestroyEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

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
