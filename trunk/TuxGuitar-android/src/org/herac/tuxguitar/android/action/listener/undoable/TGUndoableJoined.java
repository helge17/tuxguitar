package org.herac.tuxguitar.android.action.listener.undoable;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableJoined extends TGUndoableEditBase{
	
	private int doAction;
	private TGUndoableCaretState undoableState;
	private List<Object> undoables;
	
	public TGUndoableJoined(TGContext context){
		super(context);
		
		this.doAction = UNDO_ACTION;
		this.undoableState = new TGUndoableCaretState(context);
		this.undoables = new ArrayList<Object>();
	}
	
	public void addUndoableEdit(TGUndoableEdit undoable){
		this.undoables.add(undoable);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		int count = this.undoables.size();
		for(int i = 0;i < count;i++){
			TGUndoableEdit undoable = (TGUndoableEdit)this.undoables.get(i);
			undoable.redo(actionContext);
		}
		this.undoableState.redo(actionContext);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		int count = this.undoables.size();
		for(int i = (count - 1);i >= 0;i--){
			TGUndoableEdit undoable = (TGUndoableEdit)this.undoables.get(i);
			undoable.undo(actionContext);
		}
		this.undoableState.undo(actionContext);
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public TGUndoableJoined endUndo(){
		this.undoableState.endUndo();
		return this;
	}
	
	public boolean isEmpty(){
		return this.undoables.isEmpty();
	}
}
