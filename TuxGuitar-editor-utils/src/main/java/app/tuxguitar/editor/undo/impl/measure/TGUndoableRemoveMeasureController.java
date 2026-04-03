package app.tuxguitar.editor.undo.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.util.TGContext;

public class TGUndoableRemoveMeasureController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return new TGUndoableRemoveMeasure(context, ((Integer) actionContext.getAttribute(TGRemoveMeasureAction.ATTRIBUTE_MEASURE_NUMBER)).intValue());
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		if( Boolean.TRUE.equals( actionContext.getAttribute(TGRemoveMeasureAction.ATTRIBUTE_SUCCESS)) ) {
			return ((TGUndoableRemoveMeasure) undoableEdit).endUndo();
		}
		return null;
	}
}
