package org.herac.tuxguitar.editor.undo.impl;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;

import java.util.ArrayList;

/**
 * Created by tubus on 26.01.17.
 */
public class TGUndoableEditComposite implements TGUndoableEdit {

	private ArrayList<TGUndoableEdit> edits = new ArrayList<TGUndoableEdit>();

	public void addEdit(TGUndoableEdit edit) {
		edits.add(edit);
	}

	public ArrayList<TGUndoableEdit> getEdits() {
		return edits;
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		for (TGUndoableEdit edit : edits) {
			edit.redo(actionContext);
		}
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		for (TGUndoableEdit edit : edits) {
			edit.undo(actionContext);
		}
	}

	public boolean canRedo() {
		for (TGUndoableEdit edit : edits) {
			if (!edit.canRedo())
				return false;
		}
		return true;
	}

	public boolean canUndo() {
		for (TGUndoableEdit edit : edits) {
			if (!edit.canUndo())
				return false;
		}
		return true;
	}
}
