package org.herac.tuxguitar.gui.undo;

public interface UndoableEdit {
	public static final int UNDO_ACTION = 1;
	public static final int REDO_ACTION = 2;
	
	public void redo() throws CannotRedoException;
	
	public void undo() throws CannotUndoException;
	
	public boolean canRedo();
	
	public boolean canUndo();
}
