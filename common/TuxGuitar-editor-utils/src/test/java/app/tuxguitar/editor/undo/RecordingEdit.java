package app.tuxguitar.editor.undo;

import java.util.List;

import app.tuxguitar.action.TGActionContext;

/**
 * An undoable edit that writes "undo <name>" or "redo <name>" to a shared log, as told: it can say it cannot be
 * undone or redone, and it can fail.
 */
public class RecordingEdit implements TGUndoableEdit {

	private final String name;
	private final List<String> log;

	public boolean undoable = true;
	public boolean redoable = true;
	public boolean failing = false;

	public RecordingEdit(String name, List<String> log) {
		this.name = name;
		this.log = log;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if (failing) {
			throw new IllegalStateException(name);
		}
		log.add("undo " + name);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if (failing) {
			throw new IllegalStateException(name);
		}
		log.add("redo " + name);
	}

	public boolean canUndo() {
		return undoable;
	}

	public boolean canRedo() {
		return redoable;
	}
}
