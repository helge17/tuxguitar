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
 * until that pass ends. It needs the TuxGuitar Synthesizer as the MIDI Port. It succeeds when it made a take.
 */
public class TGRecordAction extends TGActionBase {

	public static final String NAME = "action.audio.record";

	public static final String ATTRIBUTE_START = "start";
	public static final String ATTRIBUTE_STOP = "stop";
	public static final String ATTRIBUTE_PASSES = "passes";

	public TGRecordAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGAudioSession session = TGAudioSession.getInstance(getContext());
		MidiPlayer player = MidiPlayer.getInstance(getContext());
		List<TGTrack> tracks = session.getArmedTracks(song);
		if (!player.isOutputPortOpen(TGAudioSession.SYNTHESIZER_PORT_KEY)) {
			return;
		}
		Long start = context.getAttribute(ATTRIBUTE_START);
		Long stop = context.getAttribute(ATTRIBUTE_STOP);
		Integer passes = context.getAttribute(ATTRIBUTE_PASSES);
		List<TGPlayedMeasure> played = TGSongTime.play(song, player.getMode(), start, stop, passes);
		TGRecording recording = new TGRecording(getSongManager(context), song, session.getDevice(), session.getPunchRange(song), player.getMode().getCurrentPercent());
		if (recording.record(tracks, played) > 0) {
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
