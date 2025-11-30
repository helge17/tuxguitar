package app.tuxguitar.app.transport;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerEvent;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;

public class TGTransportListener implements TGEventListener{

	private TGContext context;

	public TGTransportListener(TGContext context){
		this.context = context;
	}

	public void startLoop() {
		Integer fps = TGConfigManager.getInstance(context).getIntegerValue(TGConfigKeys.SCROLLING_MAX_FPS);
		int periodMs = ((fps == null) || (fps==0)) ? 25 : (1000 / fps);
		TGThreadManager.getInstance(this.context).loop(new TGThreadLoop() {
			public Long process() {
				return (processLoop() ? periodMs : BREAK);
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

				if( tgTransport.getCache().shouldRedraw()) {
					tgEditorManager.redrawPlayingNewBeat();
				} else {
					if(!tgEditorManager.isLocked()) {
						tgEditorManager.redrawPlayingThread();
					}
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
		TGEditorManager tgEditorManager = TGEditorManager.getInstance(TGTransportListener.this.context);
		tgEditorManager.asyncRunLocked(new Runnable() {
			public void run() {
				try {
					TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
					tgTransport.getCache().reset();

					TuxGuitar.getInstance().updateCache(true);

					TGTransportListener.this.startLoop();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
				}
			}
		});
	}

	public void notifyStopped() {
		TGEditorManager tgEditorManager = TGEditorManager.getInstance(TGTransportListener.this.context);
		tgEditorManager.asyncRunLocked(new Runnable() {
			public void run() {
				try {
					TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
					tgTransport.gotoPlayerPosition();
					tgTransport.getCache().reset();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(TGTransportListener.this.context).handleError(throwable);
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
