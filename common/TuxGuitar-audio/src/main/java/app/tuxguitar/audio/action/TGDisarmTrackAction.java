package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Disarms the track; its monitoring setting is kept for when it is armed again.
 */
public class TGDisarmTrackAction extends TGActionBase {

	public static final String NAME = "action.audio.track.disarm";

	public TGDisarmTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGAudioSession.getInstance(getContext()).disarm(track);
		context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
	}
}
