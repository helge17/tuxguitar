package app.tuxguitar.editor.undo.impl.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUndoableAltRepeatController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		TGMeasureHeader header = ((TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));

		return TGUndoableAltRepeat.startUndo(context, header);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		int repeatAlternative = ((Integer) actionContext.getAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE)).intValue();

		return ((TGUndoableAltRepeat) undoableEdit).endUndo(repeatAlternative);
	}
}
