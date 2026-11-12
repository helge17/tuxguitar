package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Clears an audio track's clip indicator: its input no longer shows it has clipped, until it clips again. Only audio
 * tracks have an input level.
 */
public class TGResetClipIndicatorAction extends TGActionBase {

	public static final String NAME = "action.audio.track.reset-clip-indicator";

	public TGResetClipIndicatorAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if (track.isAudio()) {
			TGAudioSession.getInstance(getContext()).getInputMeter(track).resetClipIndicator();
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
