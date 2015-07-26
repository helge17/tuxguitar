package org.herac.tuxguitar.app.transport;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayerEvent;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGTransportListener implements TGEventListener{
	
	protected Object sync;
	protected TGSynchronizer.TGRunnable startedRunnable;
	protected TGSynchronizer.TGRunnable stoppedRunnable;
	
	public TGTransportListener(){
		this.sync = new Object();
		this.startedRunnable = getStartedRunnable();
		this.stoppedRunnable = getStoppedRunnable();
	}
	
	public void notifyStarted() {
		new Thread(new Runnable() {
			public void run() throws TGException {
				try {
					TuxGuitar.getInstance().updateCache(true);
					while (TuxGuitar.getInstance().getPlayer().isRunning()) {
						synchronized( TGTransportListener.this.sync ){
							TGSynchronizer.instance().execute( TGTransportListener.this.startedRunnable );
							TGTransportListener.this.sync.wait(25);
						}
					}
					TGTransportListener.this.notifyStopped();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		}).start();
	}
	
	public void notifyStopped() {
		try {
			if(!TuxGuitar.getInstance().getDisplay().isDisposed()){
				TGSynchronizer.instance().executeLater( TGTransportListener.this.stoppedRunnable );
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	private TGSynchronizer.TGRunnable getStartedRunnable(){
		return new TGSynchronizer.TGRunnable() {
			public void run() throws TGException {
				if(TuxGuitar.getInstance().getPlayer().isRunning()){
					TuxGuitar.getInstance().redrawPlayingMode();
				}
			}
		};
	}
	
	private TGSynchronizer.TGRunnable getStoppedRunnable(){
		return new TGSynchronizer.TGRunnable() {
			public void run() throws TGException {
				TuxGuitar.getInstance().getTransport().gotoPlayerPosition();
			}
		};
	}

	public void processEvent(TGEvent event) {
		if( MidiPlayerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getProperty(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
			if( type == MidiPlayerEvent.NOTIFY_STARTED ){
				this.notifyStarted();
			} else if( type == MidiPlayerEvent.NOTIFY_STOPPED ){
				this.notifyStopped();
			}
		}
	}
}
