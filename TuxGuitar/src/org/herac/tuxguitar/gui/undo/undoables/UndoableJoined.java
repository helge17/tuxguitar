package org.herac.tuxguitar.gui.undo.undoables;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.undo.CannotRedoException;
import org.herac.tuxguitar.gui.undo.CannotUndoException;
import org.herac.tuxguitar.gui.undo.UndoableEdit;

public class UndoableJoined implements UndoableEdit{
	private int doAction;
	private UndoableCaretHelper undoCaret;
	private UndoableCaretHelper redoCaret;
	private List undoables;
	
	public UndoableJoined(){
		this.doAction = UNDO_ACTION;
		this.undoCaret = new UndoableCaretHelper();
		this.undoables = new ArrayList();
	}
	
	public void addUndoableEdit(UndoableEdit undoable){
		this.undoables.add(undoable);
	}
	
	public void redo() throws CannotRedoException {
		int count = this.undoables.size();
		for(int i = 0;i < count;i++){
			UndoableEdit undoable = (UndoableEdit)this.undoables.get(i);
			undoable.redo();
		}
		this.redoCaret.update();
		this.doAction = UNDO_ACTION;
	}
	
	public void undo() throws CannotUndoException {
		int count = this.undoables.size();
		for(int i = (count - 1);i >= 0;i--){
			UndoableEdit undoable = (UndoableEdit)this.undoables.get(i);
			undoable.undo();
		}
		this.undoCaret.update();
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public UndoableJoined endUndo(){
		this.redoCaret = new UndoableCaretHelper();
		return this;
	}
	
	public boolean isEmpty(){
		return this.undoables.isEmpty();
	}
}
