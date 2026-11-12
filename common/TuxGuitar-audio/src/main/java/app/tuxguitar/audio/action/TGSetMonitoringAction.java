package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.audio.TGMonitoring;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Sets whether an audio track's input is heard (Off, Auto, Always). Only audio tracks have monitoring.
 */
public class TGSetMonitoringAction extends TGActionBase {

	public static final String NAME = "action.audio.track.set-monitoring";

	public static final String ATTRIBUTE_MONITORING = "monitoring";

	public TGSetMonitoringAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGMonitoring monitoring = context.getAttribute(ATTRIBUTE_MONITORING);
		if (track.isAudio()) {
			TGAudioSession.getInstance(getContext()).setMonitoring(track, monitoring);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
