package org.herac.tuxguitar.android.view.dialog.channel;

import org.herac.tuxguitar.android.editor.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGChannelEditEventListener implements TGEventListener {
	
	private TGChannelEditDialog handle;
	
	public TGChannelEditEventListener(TGChannelEditDialog handle) {
		this.handle = handle;
	}

	public void updateItems() {
		this.handle.updateItems();
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			TGSynchronizer.getInstance(this.handle.findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					updateItems();
				}
			});
		}
	}
	
	public void processEvent(TGEvent event) { 
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}
