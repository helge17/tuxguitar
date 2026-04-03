package app.tuxguitar.editor.undo.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.util.TGContext;

public class TGUndoableAddMeasureController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableAddMeasure.startUndo(context, ((Integer) actionContext.getAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER)).intValue());
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		return ((TGUndoableAddMeasure) undoableEdit).endUndo();
	}
}
