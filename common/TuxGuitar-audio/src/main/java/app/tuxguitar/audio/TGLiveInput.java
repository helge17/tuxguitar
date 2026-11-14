package app.tuxguitar.audio;

import java.util.List;

import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * The input of the live audio device while it runs, as the audio features use it. Each block, each armed track's
 * input channel(s) feed its input meter: stopped, playing and recording alike. And while TuxGuitar is stopped, an
 * armed track monitored Auto or Always gets its channel opened on the TuxGuitar Synthesizer (the player opens the
 * song's channels to play, and leaves them open), so its sound chain sounds its input before anything has played:
 * when installed, and after each action.
 */
public final class TGLiveInput implements TGLiveDevice.Listener, TGEventListener, AutoCloseable {

	private final TGContext context;
	private final TGLiveDevice device;

	private TGLiveInput(TGContext context, TGLiveDevice device) {
		this.context = context;
		this.device = device;
	}

	// until closed
	public static TGLiveInput install(TGContext context, TGLiveDevice device) {
		TGLiveInput input = new TGLiveInput(context, device);
		device.addListener(input);
		TGEventManager.getInstance(context).addListener(TGActionPostExecutionEvent.EVENT_TYPE, input);
		input.openMonitoredChannels();
		return input;
	}

	// on the device's thread
	public void played(long clock, int frames) {
		TGDocumentManager document = TGDocumentManager.getInstance(this.context);
		TGAudioSession.getInstance(this.context).meter(document.getSongManager(), document.getSong(), clock, frames);
	}

	// an action ran
	public void processEvent(TGEvent event) {
		this.openMonitoredChannels();
	}

	private void openMonitoredChannels() {
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		if (!player.isRunning() && player.isOutputPortOpen(TGAudioSession.SYNTHESIZER_PORT_KEY) && this.isAMonitoredChannelClosed(player)) {
			try {
				player.updateChannels();
			} catch (MidiPlayerException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private boolean isAMonitoredChannelClosed(MidiPlayer player) {
		TGSong song = TGDocumentManager.getInstance(this.context).getSong();
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		List<Integer> open = player.getChannelRouter().getMidiChannelIds();
		for (TGTrack track : session.getArmedTracks(song)) {
			if (TGTrackSource.stopped(true, session.getMonitoring(track)) == TGTrackSource.INPUT && !open.contains(track.getChannelId())) {
				return true;
			}
		}
		return false;
	}

	public void close() {
		this.device.removeListener(this);
		TGEventManager.getInstance(this.context).removeListener(TGActionPostExecutionEvent.EVENT_TYPE, this);
	}
}
