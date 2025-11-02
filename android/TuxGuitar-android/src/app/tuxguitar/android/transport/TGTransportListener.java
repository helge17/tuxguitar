package app.tuxguitar.android.transport;

import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
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
		TGEditorManager tgEditorManager = TGEditorManager.getInstance(TGTransportListener.this.context);
		tgEditorManager.asyncRunLocked(new Runnable() {
			public void run() {
				try {
					TGTransport tgTransport = TGTransport.getInstance(TGTransportListener.this.context);
					tgTransport.getCache().reset();

					TGActivity activity = TGActivityController.getInstance(TGTransportListener.this.context).getActivity();
					if( activity != null ) {
						activity.updateCache(true);
						activity.setDisplayOn(true);
					}

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
					TGActivity activity = TGActivityController.getInstance(TGTransportListener.this.context).getActivity();
					if( activity != null ) {
						activity.setDisplayOn(false);
					}
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
