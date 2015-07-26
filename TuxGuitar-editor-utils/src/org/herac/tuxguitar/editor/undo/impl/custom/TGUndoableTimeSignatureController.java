package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTimeSignatureController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableTimeSignature.startUndo(context);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		TGMeasure measure = ((TGMeasure) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));		
		TGTimeSignature timeSignature = ((TGTimeSignature) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE));
		boolean applyToEnd = ((Boolean) actionContext.getAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END)).booleanValue();
		
		return ((TGUndoableTimeSignature) undoableEdit).endUndo(timeSignature, measure.getStart(), applyToEnd);
	}
}
