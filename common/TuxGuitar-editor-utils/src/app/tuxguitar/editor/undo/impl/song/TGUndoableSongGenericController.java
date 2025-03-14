package app.tuxguitar.editor.undo.impl.song;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.util.TGContext;

public class TGUndoableSongGenericController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableSongGeneric.startUndo(context);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		return ((TGUndoableSongGeneric) undoableEdit).endUndo();
	}
}
