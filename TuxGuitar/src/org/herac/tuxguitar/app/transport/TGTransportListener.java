package org.herac.tuxguitar.app.transport;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayerEvent;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGTransportListener implements TGEventListener{
	
	private TGContext context;
	private Object sync;
	
	public TGTransportListener(TGContext context){
		this.context = context;
		this.sync = new Object();
	}
	
	public void notifyStarted() {
		new Thread(new Runnable() {
			public void run() {
				try {
					TuxGuitar.getInstance().updateCache(true);
					while( TuxGuitar.getInstance().getPlayer().isRunning() ) {
						
						TuxGuitar.getInstance().getEditorCache().updatePlayMode();
						if( TuxGuitar.getInstance().getEditorCache().shouldRedraw() ) {
							TuxGuitar.getInstance().getEditorManager().redrawPlayingNewBeat();
						} else {
							TuxGuitar.getInstance().getEditorManager().redrawPlayingThread();
						}
						
						synchronized( TGTransportListener.this.sync ){
							TGTransportListener.this.sync.wait(25);
						}
					}
					TGTransportListener.this.notifyStopped();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
				}
			}
		}).start();
	}
	
	public void notifyStopped() {
		new Thread(new Runnable() {
			public void run() {
				try {
					TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
					tgTransport.gotoPlayerPosition();
					TuxGuitar.getInstance().getEditorCache().reset();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
				}
			}
		}).start();
	}
	
	public void processEvent(TGEvent event) {
		if( MidiPlayerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
			if( type == MidiPlayerEvent.NOTIFY_STARTED ){
				this.notifyStarted();
			} else if( type == MidiPlayerEvent.NOTIFY_STOPPED ){
				this.notifyStopped();
			}
		}
	}
}
