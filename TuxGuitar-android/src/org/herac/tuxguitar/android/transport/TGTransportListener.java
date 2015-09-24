package org.herac.tuxguitar.android.transport;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayerEvent;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGTransportListener implements TGEventListener{
	
	private TGContext context;
	
	protected Object sync;
//	protected TGSynchronizer.TGRunnable startedRunnable;
//	protected TGSynchronizer.TGRunnable stoppedRunnable;
	
	public TGTransportListener(TGContext context){
		this.context = context;
		
		this.sync = new Object();
//		this.startedRunnable = getStartedRunnable();
//		this.stoppedRunnable = getStoppedRunnable();
	}
	
	public void notifyStarted() {
		new Thread(new Runnable() {
			public void run() {
				try {
					TuxGuitar tuxguitar = TuxGuitar.getInstance(TGTransportListener.this.context);
					tuxguitar.getTransport().getCache().reset();
					tuxguitar.updateCache(true);
					while( tuxguitar.getPlayer().isRunning() ) {
						
						TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
						tgTransport.getCache().updatePlayMode();
						if( tgTransport.getCache().shouldRedraw() ) {
							tuxguitar.getEditorManager().redrawPlayingNewBeat();
						} else {
							tuxguitar.getEditorManager().redrawPlayingThread();
						}
						synchronized( TGTransportListener.this.sync ){
//							TGSynchronizer.getInstance().executeLater( TGTransportListener.this.startedRunnable );
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
					tgTransport.getCache().reset();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
				}
			}
		}).start();
//		try {
//			TGSynchronizer.getInstance().executeLater( TGTransportListener.this.stoppedRunnable );
//		} catch (Throwable throwable) {
//			TGErrorManager.getInstance(this.context).handleError(throwable);
//		}
	}
//	
//	private TGSynchronizer.TGRunnable getStartedRunnable(){
//		return new Runnable() {
//			public void run() throws TGException {
//				TuxGuitar tuxguitar = TuxGuitar.getInstance(TGTransportListener.this.context);
//				if( tuxguitar.getPlayer().isRunning()){
//					TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
//					tgTransport.getCache().updatePlayMode();
//					if( tgTransport.getCache().shouldRedraw() ) {
//						tuxguitar.getEditorManager().redrawPlayingNewBeat();
//					} else {
//						tuxguitar.getEditorManager().redrawPlayingThread();
//					}
//				}
//			}
//		};
//	}
	
//	private TGSynchronizer.TGRunnable getStoppedRunnable(){
//		return new Runnable() {
//			public void run() throws TGException {
//				TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
//				tgTransport.gotoPlayerPosition();
//				tgTransport.getCache().reset();
//			}
//		};
//	}

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
