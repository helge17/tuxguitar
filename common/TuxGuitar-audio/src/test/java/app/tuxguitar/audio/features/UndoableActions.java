package app.tuxguitar.audio.features;

import java.util.IdentityHashMap;
import java.util.Map;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.util.TGContext;

/**
 * As the app's undoable listener (desktop's TGUndoableActionListener) keeps an undo step for each action its config maps
 * to a controller, however the action is run: here for an action the module runs by itself (a gesture's change of the
 * song), on an action context of its own.
 */
final class UndoableActions {

	private final TGContext context;
	private final String action;
	private final TGUndoableActionController controller;
	private final Map<TGActionContext, TGUndoableEdit> started = new IdentityHashMap<TGActionContext, TGUndoableEdit>();

	private UndoableActions(TGContext context, String action, TGUndoableActionController controller) {
		this.context = context;
		this.action = action;
		this.controller = controller;
	}

	static void install(TGContext context, String action, TGUndoableActionController controller) {
		UndoableActions undoable = new UndoableActions(context, action, controller);
		TGActionManager manager = TGActionManager.getInstance(context);
		manager.addPreExecutionListener(undoable::before);
		manager.addPostExecutionListener(undoable::after);
	}

	private boolean isTheAction(TGEvent event) {
		return this.action.equals(event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID));
	}

	private static TGActionContext source(TGEvent event) {
		return event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
	}

	private void before(TGEvent event) {
		if (this.isTheAction(event)) {
			this.started.put(source(event), this.controller.startUndoable(this.context, source(event)));
		}
	}

	private void after(TGEvent event) {
		if (this.isTheAction(event)) {
			this.keep(this.controller.endUndoable(this.context, source(event), this.started.remove(source(event))));
		}
	}

	// a refused change is no undo step
	private void keep(TGUndoableEdit edit) {
		if (edit != null) {
			TGUndoableManager.getInstance(this.context).addEdit(edit);
		}
	}
}
