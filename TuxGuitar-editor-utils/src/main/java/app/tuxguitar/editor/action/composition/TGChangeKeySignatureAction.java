package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGChangeKeySignatureAction extends TGActionBase {

	public static final String NAME = "action.composition.change-key-signature";

	public static final String ATTRIBUTE_APPLY_TO_END = "applyToEnd";
	public static final String ATTRIBUTE_KEY_SIGNATURE = "keySignature";
	public static final String ATTRIBUTE_APPLY_TO_SELECTION = "changeKeySignature-applyToSelection";

	public TGChangeKeySignatureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		int keySignature = ((Integer) context.getAttribute(ATTRIBUTE_KEY_SIGNATURE)).intValue();
		Boolean applyToEnd = ((Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_END));

		TGSongManager songManager = getSongManager(context);
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));

		Boolean applyToSelection = (Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_SELECTION);
		TGBeatRange beatRange = (TGBeatRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		
		if (Boolean.TRUE.equals(applyToSelection) && (beatRange != null) && !beatRange.isEmpty()) {
			songManager.getTrackManager().changeKeySignature(track, beatRange.getMeasures(), keySignature);
		} else if (measure != null) {
			songManager.getTrackManager().changeKeySignature(track, measure, keySignature, Boolean.TRUE.equals(applyToEnd));
		}
	}
}
