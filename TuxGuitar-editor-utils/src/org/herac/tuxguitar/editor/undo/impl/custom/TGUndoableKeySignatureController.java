package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
