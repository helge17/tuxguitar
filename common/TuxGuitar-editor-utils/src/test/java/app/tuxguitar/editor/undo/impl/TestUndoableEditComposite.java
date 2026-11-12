package app.tuxguitar.editor.undo.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.editor.undo.RecordingEdit;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;

/**
 * Pins TGUndoableEditComposite: it can be undone (or redone) when every edit in it can, and undoing or redoing it
 * undoes or redoes every edit in the order they were added. Undoing in that same order, not the reverse, looks
 * wrong for edits that depend on each other; it is pinned as it is.
 *
 * Each edit writes what it does to the log, read back as "undo a, redo a, ...".
 */
public class TestUndoableEditComposite {

	private final List<String> log = new ArrayList<String>();
	private final TGUndoableEditComposite composite = new TGUndoableEditComposite();
	private final RecordingEdit first = add("a");
	private final RecordingEdit second = add("b");

	private RecordingEdit add(String name) {
		return RecordingEdit.addedTo(composite::addEdit, name, log);
	}

	private String can() {
		return "undo " + composite.canUndo() + ", redo " + composite.canRedo();
	}

	@Test
	public void itCanWhenEveryEditCan() {
		assertEquals("undo true, redo true", can());
	}

	@Test
	public void itCannotWhenOneEditCannot() {
		second.undoable = false;
		first.redoable = false;

		assertEquals("undo false, redo false", can());
	}

	@Test
	public void undoAndRedoGoThroughTheEditsInTheOrderAdded() throws TGCannotUndoException, TGCannotRedoException {
		composite.undo(null);
		composite.redo(null);

		assertEquals("undo a, undo b, redo a, redo b", String.join(", ", log));
	}
}
