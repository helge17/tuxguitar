package app.tuxguitar.android.action.listener.undoable;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.util.TGContext;

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
