package org.herac.tuxguitar.android.action.listener.undoable;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionPostExecutionEvent;
import org.herac.tuxguitar.action.TGActionPreExecutionEvent;
import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.TGActionMap;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableActionListener implements TGEventListener {
	
	private TGContext context;
	private TGUndoableJoined undoable;
	private Map<Object, TGUndoableEdit> undoableEdits;
	private TGActionMap<TGUndoableActionController> controllers;
	private Integer level;
	
	public TGUndoableActionListener(TGContext context){
		this.context = context;
		this.controllers = new TGActionMap<TGUndoableActionController>();
		this.undoableEdits = new HashMap<Object, TGUndoableEdit>();
		this.reset();
	}
	
	public void reset() {
		this.level = 0;
		this.undoable = null;
		this.undoableEdits.clear();
	}
	
	public TGActionMap<TGUndoableActionController> getControllers() {
		return controllers;
	}
	
	public void addUndoableEdit(Integer level, TGUndoableEdit undoableEdit) {
		this.undoableEdits.put(level, undoableEdit);
	}
	
	public TGUndoableEdit getUndoableEdit(Integer level) {
		if( this.undoableEdits.containsKey(level)) {
			return this.undoableEdits.get(level);
		}
		return null;
	}
	
	public void removeUndoableEdit(Integer level) {
		if( this.undoableEdits.containsKey(level)) {
			this.undoableEdits.remove(level);
		}
	}
	
	public void processPreExecution(String actionId, TGActionContext actionContext) {
		TGUndoableActionController controller = this.controllers.get(actionId);
		if( controller != null ) {
			if( this.undoable == null ) {
				this.undoable = new TGUndoableJoined(this.context);
			}
			
			TGUndoableEdit undoableEdit = controller.startUndoable(this.context, actionContext);
			if( undoableEdit != null ) {
				this.undoableEdits.put(this.level, undoableEdit);
			}
		}
		
		this.level ++;
	}
	
	public void processPostExecution(String actionId, TGActionContext actionContext) {
		this.level --;
		
		TGUndoableEdit undoableEdit = this.getUndoableEdit(this.level);
		if( undoableEdit != null ) {
			TGUndoableActionController controller = this.controllers.get(actionId);
			if( controller != null ) {
				undoableEdit = controller.endUndoable(this.context, actionContext, undoableEdit);
				if( undoableEdit != null ) {
					this.undoable.addUndoableEdit(undoableEdit);
				}
			}
		}
		
		if( this.level == 0 && this.undoable != null && !this.undoable.isEmpty() ) {
			TuxGuitar.getInstance(this.context).getUndoableManager().addEdit(this.undoable.endUndo());
			
			this.reset();
		}
	}
	
	public void processPreExecution(TGEvent event) {
		String actionId = (String) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		TGActionContext actionContext = (TGActionContext) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT);
		
		if(!this.isByPassUndoable(actionContext)) {
			this.processPreExecution(actionId, actionContext);
		}
	}
	
	public void processPostExecution(TGEvent event) {
		String actionId = (String) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);
		TGActionContext actionContext = (TGActionContext) event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT);
		
		if(!this.isByPassUndoable(actionContext)) {
			this.processPostExecution(actionId, actionContext);
		}
	}
	
	public void processError(TGEvent event) {
		this.reset();
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
		else if( TGActionErrorEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processError(event);
		}
	}
}
