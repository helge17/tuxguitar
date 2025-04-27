package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUndoableOpenRepeatController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);

		return TGUndoableOpenRepeat.startUndo(context, header);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		return ((TGUndoableOpenRepeat) undoableEdit).endUndo();
	}
}
