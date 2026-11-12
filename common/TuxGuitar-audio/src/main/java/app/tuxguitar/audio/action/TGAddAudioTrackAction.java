package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Adds an audio track at the end of the song, recording from the given input channel of the audio device.
 */
public class TGAddAudioTrackAction extends TGActionBase {

	public static final String NAME = "action.audio.track.add";

	public static final String ATTRIBUTE_INPUT_CHANNEL = "inputChannel";

	public TGAddAudioTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		Integer inputChannel = context.getAttribute(ATTRIBUTE_INPUT_CHANNEL);
		TGTrack track = getSongManager(context).addAudioTrack(song, TGAudioInput.mono(inputChannel));
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
	}
}
