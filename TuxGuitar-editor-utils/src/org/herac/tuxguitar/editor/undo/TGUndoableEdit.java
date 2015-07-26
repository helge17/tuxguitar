package org.herac.tuxguitar.editor.undo;

public interface TGUndoableEdit {
	public static final int UNDO_ACTION = 1;
	public static final int REDO_ACTION = 2;
	
	public void redo() throws TGCannotRedoException;
	
	public void undo() throws TGCannotUndoException;
	
	public boolean canRedo();
	
	public boolean canUndo();
}
