package app.tuxguitar.editor.undo;

import app.tuxguitar.action.TGActionContext;

public interface TGUndoableEdit {

	public static final int UNDO_ACTION = 1;
	public static final int REDO_ACTION = 2;

	public void redo(TGActionContext actionContext) throws TGCannotRedoException;

	public void undo(TGActionContext actionContext) throws TGCannotUndoException;

	public boolean canRedo();

	public boolean canUndo();
}
