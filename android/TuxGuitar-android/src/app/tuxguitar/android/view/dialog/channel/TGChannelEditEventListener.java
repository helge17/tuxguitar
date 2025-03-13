package app.tuxguitar.android.view.dialog.channel;

import app.tuxguitar.android.view.util.TGProcess;
import app.tuxguitar.android.view.util.TGSyncProcessLocked;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;

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
				if( TGChannelEditEventListener.this.handle.isReady() ) {
					TGChannelEditEventListener.this.handle.updateItems();
				}
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
