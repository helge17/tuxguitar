package app.tuxguitar.audio.action;

import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.audio.TGPlayedMeasure;
import app.tuxguitar.audio.TGRecording;
import app.tuxguitar.audio.TGSongTime;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Records the armed tracks while the song plays from a position until another one is reached in the last pass, or
 * until that pass ends. It needs an armed track and the TuxGuitar Synthesizer as the MIDI Port, and then succeeds,
 * whether or not it made a take (ATTRIBUTE_TAKES says how many). What was
 * heard while recording is left in the context as TGPlayAction.ATTRIBUTE_RENDER. While it runs, the session says a
 * recording is under way.
 */
public class TGRecordAction extends TGActionBase {

	public static final String NAME = "action.audio.record";

	public static final String ATTRIBUTE_START = "start";
	public static final String ATTRIBUTE_STOP = "stop";
	public static final String ATTRIBUTE_PASSES = "passes";
	// how many takes it made
	public static final String ATTRIBUTE_TAKES = "takes";

	public TGRecordAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGAudioSession session = TGAudioSession.getInstance(getContext());
		MidiPlayer player = MidiPlayer.getInstance(getContext());
		List<TGTrack> tracks = session.getArmedTracks(song);
		if (tracks.isEmpty() || !player.isOutputPortOpen(TGAudioSession.SYNTHESIZER_PORT_KEY)) {
			return;
		}
		TGRecordingVeto.getInstance(getContext());
		session.setRecording(true);
		try {
			this.record(context, song, session, tracks);
		} finally {
			session.setRecording(false);
		}
	}

	private void record(TGActionContext context, TGSong song, TGAudioSession session, List<TGTrack> tracks) {
		Long start = context.getAttribute(ATTRIBUTE_START);
		Long stop = context.getAttribute(ATTRIBUTE_STOP);
		Integer passes = context.getAttribute(ATTRIBUTE_PASSES);
		List<TGPlayedMeasure> played = TGSongTime.play(song, MidiPlayer.getInstance(getContext()).getMode(), start, stop, passes);
		context.setAttribute(TGPlayAction.ATTRIBUTE_RENDER, TGPlayAction.play(getContext(), context, getSongManager(context), true));
		TGRecording recording = new TGRecording(getSongManager(context), song, session.getDevice(), session.getPunchRange(song), session.getRoundTrip());
		context.setAttribute(ATTRIBUTE_TAKES, recording.record(tracks, played));
		context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
	}
}
