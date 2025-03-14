package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUndoableCloseRepeatController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);

		return TGUndoableCloseRepeat.startUndo(context, header);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		int repeatCount = ((Integer) actionContext.getAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT)).intValue();

		return ((TGUndoableCloseRepeat) undoableEdit).endUndo(repeatCount);
	}
}
