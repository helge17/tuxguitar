package org.herac.tuxguitar.app.action.listener.undoable;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.app.action.TGActionMap;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;

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
