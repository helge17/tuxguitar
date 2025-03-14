package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGChangeKeySignatureAction extends TGActionBase {

	public static final String NAME = "action.composition.change-key-signature";

	public static final String ATTRIBUTE_APPLY_TO_END = "applyToEnd";
	public static final String ATTRIBUTE_KEY_SIGNATURE = "keySignature";

	public TGChangeKeySignatureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		int keySignature = ((Integer) context.getAttribute(ATTRIBUTE_KEY_SIGNATURE)).intValue();
		boolean applyToEnd = ((Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_END)).booleanValue();

		TGSongManager songManager = getSongManager(context);
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));

		songManager.getTrackManager().changeKeySignature(track, measure, keySignature, applyToEnd);
	}
}
