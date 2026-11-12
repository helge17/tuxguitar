package app.tuxguitar.audio.undo;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackGenericController;
import app.tuxguitar.util.TGContext;

/**
 * Undoes a change of the takes of a track (choosing, deleting) by restoring the track as it was; a refused change is
 * no undo step. The snapshots of the track share its takes.
 */
public class TGUndoableTakesController implements TGUndoableActionController {

	private final TGUndoableTrackGenericController track = new TGUndoableTrackGenericController();

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return this.track.startUndoable(context, actionContext);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		if (Boolean.TRUE.equals(actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS))) {
			return this.track.endUndoable(context, actionContext, undoableEdit);
		}
		return null;
	}
}
