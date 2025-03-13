package app.tuxguitar.android.action.listener.undoable;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.android.action.TGActionMap;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;

public class TGUndoableActionListener implements TGEventListener {

	private TGContext context;
	private TGActionMap<TGUndoableActionController> controllers;

	public TGUndoableActionListener(TGContext context){
		this.context = context;
		this.controllers = new TGActionMap<TGUndoableActionController>();
	}

	public TGActionMap<TGUndoableActionController> getControllers() {
		return controllers;
	}

	public void processPreExecution(String actionId, TGActionContext actionContext) {
		TGUndoableContext undoableContext = TGUndoableContext.getInstance(actionContext);

		TGUndoableActionController controller = this.controllers.get(actionId);
		if( controller != null ) {
			if( undoableContext.getUndoable() == null ) {
				undoableContext.setUndoable(new TGUndoableJoined(this.context));
			}

			TGUndoableEdit undoableEdit = controller.startUndoable(this.context, actionContext);
			if( undoableEdit != null ) {
				undoableContext.addUndoableToCurrentLevel(undoableEdit);
			}
		}

		undoableContext.incrementLevel();
	}

	public void processPostExecution(String actionId, TGActionContext actionContext) {
		TGUndoableContext undoableContext = TGUndoableContext.getInstance(actionContext);
		undoableContext.decrementLevel();

		TGUndoableEdit undoableEdit = undoableContext.getUndoableFromCurrentLevel();
		if( undoableEdit != null ) {
			TGUndoableActionController controller = this.controllers.get(actionId);
			if( controller != null ) {
				undoableEdit = controller.endUndoable(this.context, actionContext, undoableEdit);
				if( undoableEdit != null ) {
					undoableContext.getUndoable().addUndoableEdit(undoableEdit);
				}
			}
		}

		if( undoableContext.getLevel() == 0 && undoableContext.getUndoable() != null && !undoableContext.getUndoable().isEmpty() ) {
			TGUndoableManager.getInstance(this.context).addEdit(undoableContext.getUndoable().endUndo());

			undoableContext.reset();
		}
	}

	public void processPreExecution(TGEvent event) {
		String actionId = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		TGActionContext actionContext = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT);

		if(!this.isByPassUndoable(actionContext)) {
			this.processPreExecution(actionId, actionContext);
		}
	}

	public void processPostExecution(TGEvent event) {
		String actionId = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		TGActionContext actionContext = event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT);

		if(!this.isByPassUndoable(actionContext)) {
			this.processPostExecution(actionId, actionContext);
		}
	}

	public boolean isByPassUndoable(TGActionContext actionContext) {
		return Boolean.TRUE.equals(actionContext.getAttribute(TGUndoableEditBase.ATTRIBUTE_BY_PASS_UNDOABLE));
	}

	public void processEvent(final TGEvent event) {
		if( TGActionPreExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processPreExecution(event);
		}
		else if( TGActionPostExecutionEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processPostExecution(event);
		}
	}
}
