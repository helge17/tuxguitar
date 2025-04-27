package app.tuxguitar.editor.undo;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.util.TGContext;

public interface TGUndoableActionController {

	TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext);

	TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit);
}
