package app.tuxguitar.app.backingtrack;

import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerEvent;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;

public class TGBackingTrackListener implements TGEventListener {

	private static final int SYNC_PERIOD_MS = 100;
	private static final long DRIFT_THRESHOLD_MS = 100L;

	private TGContext context;
	private volatile boolean syncActive;

	public TGBackingTrackListener(TGContext context) {
		this.context = context;
	}

	public void processEvent(final TGEvent event) {
		if( MidiPlayerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			final int type = ((Integer) event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
			TGThreadManager.getInstance(this.context).start(new Runnable() {
				public void run() {
					TGBackingTrackListener.this.handleNotification(type);
				}
			});
		}
	}

	private void handleNotification(int type) {
		try {
			MidiPlayer midiPlayer = MidiPlayer.getInstance(this.context);
			TGBackingTrackManager manager = TGBackingTrackManager.getInstance(this.context);
			if( type == MidiPlayerEvent.NOTIFY_STOPPED ) {
				if( !midiPlayer.isRunning() ) {
					manager.pause();
				}
			} else if( type == MidiPlayerEvent.NOTIFY_STARTED
					|| type == MidiPlayerEvent.NOTIFY_COUNT_DOWN_STOPPED
					|| type == MidiPlayerEvent.NOTIFY_LOOP ) {
				if( !midiPlayer.isRunning() || midiPlayer.getCountDown().isEnabled() ) {
					return;
				}
				if( midiPlayer.getMode().getCurrentPercent() != MidiPlayerMode.SIMPLE_DEFAULT_TEMPO_PERCENT ) {
					manager.pause();
					return;
				}
				manager.clearFailed();
				manager.preloadIfNeeded();
				if( manager.hasError() ) {
					manager.reportError();
					return;
				}
				Long timestamp = midiPlayer.getCurrentTimestamp();
				long positionMs = (timestamp != null && timestamp > 0L) ? timestamp : 0L;
				long durationMs = manager.getDurationMs();
				if( durationMs > 0L && positionMs >= durationMs ) {
					manager.pause();
					return;
				}
				manager.playAt(positionMs);
				this.startSyncLoop();
			}
		} catch (Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}

	private void startSyncLoop() {
		synchronized( this ) {
			if( this.syncActive ) {
				return;
			}
			this.syncActive = true;
		}
		TGThreadManager.getInstance(this.context).loop(new TGThreadLoop() {
			public Long process() {
				return (processSync() ? SYNC_PERIOD_MS : BREAK);
			}
		});
	}

	private boolean processSync() {
		try {
			MidiPlayer midiPlayer = MidiPlayer.getInstance(this.context);
			if( !midiPlayer.isRunning() ) {
				return this.exitSyncLoop();
			}
			TGBackingTrackManager manager = TGBackingTrackManager.getInstance(this.context);
			if( midiPlayer.getMode().getCurrentPercent() != MidiPlayerMode.SIMPLE_DEFAULT_TEMPO_PERCENT ) {
				manager.pause();
				return true;
			}
			Long timestamp = midiPlayer.getCurrentTimestamp();
			long targetMs = (timestamp != null && timestamp > 0L) ? timestamp : 0L;
			long durationMs = manager.getDurationMs();
			if( durationMs > 0L && targetMs >= durationMs ) {
				manager.pause();
				return true;
			}
			long driftMs = manager.getPositionMs() - targetMs;
			if( !manager.isPlaying() || Math.abs(driftMs) > DRIFT_THRESHOLD_MS ) {
				manager.playAt(targetMs);
			}
		} catch (Throwable throwable) {
			synchronized( this ) {
				this.syncActive = false;
			}
			TGErrorManager.getInstance(this.context).handleError(throwable);
			return false;
		}
		return true;
	}

	private boolean exitSyncLoop() {
		MidiPlayer midiPlayer = MidiPlayer.getInstance(this.context);
		synchronized( this ) {
			if( !midiPlayer.isRunning() ) {
				this.syncActive = false;
				return false;
			}
		}
		return true;
	}
}
