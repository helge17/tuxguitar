package app.tuxguitar.audio.undo;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.util.TGContext;

/**
 * Undoes an action the way another controller does, but only when the action reported success: a refused change is
 * no undo step. The app maps it, around the track's controller, to choosing and deleting takes (whose snapshots share
 * the takes), and around the channel's controller to setting input channels.
 */
public class TGUndoableOnSuccess implements TGUndoableActionController {

	private final TGUndoableActionController controller;

	public TGUndoableOnSuccess(TGUndoableActionController controller) {
		this.controller = controller;
	}

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return this.controller.startUndoable(context, actionContext);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		if (Boolean.TRUE.equals(actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS))) {
			return this.controller.endUndoable(context, actionContext, undoableEdit);
		}
		return null;
	}
}
