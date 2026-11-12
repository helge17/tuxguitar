package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Arms the track to record from its input channel. Only audio tracks can be armed.
 */
public class TGArmTrackAction extends TGActionBase {

	public static final String NAME = "action.audio.track.arm";

	public TGArmTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if (track.isAudio()) {
			TGAudioSession.getInstance(getContext()).arm(track);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
