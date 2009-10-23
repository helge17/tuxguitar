package org.herac.tuxguitar.gui.transport;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiPlayerListener;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGTransportListener implements MidiPlayerListener{
	
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
			public void run() {
				try {
					TuxGuitar.instance().updateCache(true);
					while (TuxGuitar.instance().getPlayer().isRunning()) {
						synchronized( TGTransportListener.this.sync ){
							TGSynchronizer.instance().addRunnable( TGTransportListener.this.startedRunnable );
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
			if(!TuxGuitar.instance().getDisplay().isDisposed()){
				TGSynchronizer.instance().runLater( TGTransportListener.this.stoppedRunnable );
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void notifyLoop(){
		// Not implemented
	}
	
	private TGSynchronizer.TGRunnable getStartedRunnable(){
		return new TGSynchronizer.TGRunnable() {
			public void run() {
				if(TuxGuitar.instance().getPlayer().isRunning()){
					TuxGuitar.instance().redrawPlayingMode();
				}
			}
		};
	}
	
	private TGSynchronizer.TGRunnable getStoppedRunnable(){
		return new TGSynchronizer.TGRunnable() {
			public void run() {
				TuxGuitar.instance().getTransport().gotoPlayerPosition();
			}
		};
	}
}
