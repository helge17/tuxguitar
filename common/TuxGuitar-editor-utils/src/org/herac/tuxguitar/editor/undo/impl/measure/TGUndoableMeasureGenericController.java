package org.herac.tuxguitar.editor.undo.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableMeasureGenericController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableMeasureGeneric.startUndo(context, (TGMeasure) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		return ((TGUndoableMeasureGeneric) undoableEdit).endUndo((TGMeasure) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
	}
}
