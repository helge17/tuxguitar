package org.herac.tuxguitar.android.view.dialog.channel;

import org.herac.tuxguitar.android.view.util.TGProcess;
import org.herac.tuxguitar.android.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGChannelEditEventListener implements TGEventListener {
	
	private TGChannelEditDialog handle;
	private TGProcess updateItems;
	
	public TGChannelEditEventListener(TGChannelEditDialog handle) {
		this.handle = handle;
		this.createSyncProcesses();
	}
	
	public void createSyncProcesses() {
		this.updateItems = new TGSyncProcessLocked(this.handle.findContext(), new Runnable() {
			public void run() {
				TGChannelEditEventListener.this.handle.updateItems();
			}
		});
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItems.process();
		}
	}
	
	public void processEvent(TGEvent event) { 
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}
