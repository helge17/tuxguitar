package org.herac.tuxguitar.android.transport;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerEvent;
import org.herac.tuxguitar.thread.TGThreadLoop;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGTransportListener implements TGEventListener{
	
	private TGContext context;
	
	public TGTransportListener(TGContext context){
		this.context = context;
	}
	
	public void startLoop() {
		TGThreadManager.getInstance(this.context).loop(new TGThreadLoop() {
			public Long process() {
				return (processLoop() ? 25 : BREAK);
			}
		});
	}
	
	public boolean processLoop() {
		try {
			TGEditorManager tgEditorManager = TGEditorManager.getInstance(TGTransportListener.this.context);
			TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
			MidiPlayer midiPlayer = MidiPlayer.getInstance(TGTransportListener.this.context);
			if( midiPlayer.isRunning() ) {
				tgEditorManager.lock();
				try {
					tgTransport.getCache().updatePlayMode();
				} finally {
					tgEditorManager.unlock();
				}
				
				if( tgTransport.getCache().shouldRedraw() ) {
					tgEditorManager.redrawPlayingNewBeat();
				}
				return true;
			} else {
				TGTransportListener.this.notifyStopped();
			}
		} catch (Throwable throwable) {
			TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
		}
		return false;
	}
	
	public void notifyStarted() {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				try {
					TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
					tgTransport.getCache().reset();
					
					TGActivity activity = TGActivityController.getInstance(TGTransportListener.this.context).getActivity();
					if( activity != null ) {
						activity.updateCache(true);
					}
					
					TGTransportListener.this.startLoop();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
				}
			}
		});
	}
	
	public void notifyStopped() {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				TGEditorManager tgEditorManager = TGEditorManager.getInstance(TGTransportListener.this.context);
				try {
					tgEditorManager.lock();
					
					TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
					tgTransport.gotoPlayerPosition();
					tgTransport.getCache().reset();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
				} finally {
					tgEditorManager.unlock();
				}
			}
		});
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
