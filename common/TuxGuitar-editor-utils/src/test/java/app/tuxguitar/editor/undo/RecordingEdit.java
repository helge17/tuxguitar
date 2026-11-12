package app.tuxguitar.editor.undo;

import java.util.List;
import java.util.function.Consumer;

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

	// a new edit, given to the adder (an undo manager or a composite edit)
	public static RecordingEdit addedTo(Consumer<TGUndoableEdit> adder, String name, List<String> log) {
		RecordingEdit edit = new RecordingEdit(name, log);
		adder.accept(edit);
		return edit;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		record("undo");
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		record("redo");
	}

	private void record(String what) {
		if (failing) {
			throw new IllegalStateException(name);
		}
		log.add(what + " " + name);
	}

	public boolean canUndo() {
		return undoable;
	}

	public boolean canRedo() {
		return redoable;
	}
}
