package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGUndoableKeySignatureController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGTrack track = ((TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasure measure = ((TGMeasure) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));

		return TGUndoableKeySignature.startUndo(context, track, measure);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		int keySignature = ((Integer) actionContext.getAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE)).intValue();
		boolean applyToEnd = ((Boolean) actionContext.getAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END)).booleanValue();

		return ((TGUndoableKeySignature) undoableEdit).endUndo(keySignature, applyToEnd);
	}
}
