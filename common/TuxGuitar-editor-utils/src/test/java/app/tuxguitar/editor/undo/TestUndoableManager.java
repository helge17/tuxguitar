package app.tuxguitar.editor.undo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionException;
import app.tuxguitar.editor.action.SongActionFixture;

/**
 * Pins TGUndoableManager, driven as the app drives it: edits are added as actions run, and the undo and redo actions
 * undo the last edit done and redo the first edit undone, when the edit says it can. A new edit drops the edits
 * undone; only the last 100 edits are kept. Undoing or redoing without an edit, or with an edit that fails, is an
 * error, and the failing edit stays where it was.
 *
 * Each edit writes what it does to the log, read back as "undo a, redo a, ...".
 */
public class TestUndoableManager {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGUndoableManager manager = TGUndoableManager.getInstance(fixture.context);
	private final List<String> log = new ArrayList<String>();

	private RecordingEdit add(String name) {
		RecordingEdit edit = new RecordingEdit(name, log);
		manager.addEdit(edit);
		return edit;
	}

	private String log() {
		return String.join(", ", log);
	}

	@Test
	public void undoGoesBackFromTheLastEditAndRedoForwardAgain() {
		add("a");
		add("b");

		fixture.undo();
		fixture.undo();
		fixture.redo();
		fixture.redo();

		assertEquals("undo b, undo a, redo a, redo b", log());
	}

	@Test
	public void undoAndRedoStopAtTheEnds() {
		add("a");

		fixture.redo();
		fixture.undo();
		fixture.undo();
		fixture.redo();
		fixture.redo();

		assertEquals("undo a, redo a", log());
	}

	@Test
	public void anEditThatCannotBeUndoneIsLeftAsIs() {
		add("a");
		add("b").undoable = false;

		fixture.undo();

		assertEquals("false / ", manager.canUndo() + " / " + log());
	}

	@Test
	public void anEditThatCannotBeRedoneIsLeftAsIs() {
		add("a").redoable = false;
		fixture.undo();

		fixture.redo();

		assertEquals("false / undo a", manager.canRedo() + " / " + log());
	}

	@Test
	public void aNewEditDropsTheEditsUndone() {
		add("a");
		add("b");
		fixture.undo();
		add("c");

		fixture.redo();
		fixture.undo();
		fixture.undo();

		assertEquals("undo b, undo c, undo a", log());
	}

	@Test
	public void onlyTheLastHundredEditsAreKept() {
		for (int i = 0; i <= 100; i++) {
			add(String.valueOf(i));
		}

		for (int i = 0; i <= 100; i++) {
			fixture.undo();
		}

		assertEquals("100 undone, the last of edit 1", log.size() + " undone, the last of edit " + log.get(log.size() - 1).substring("undo ".length()));
	}

	@Test
	public void discardingAllEditsLeavesNothingToUndo() {
		add("a");

		manager.discardAllEdits();

		assertEquals("false false", manager.canUndo() + " " + manager.canRedo());
	}

	@Test
	public void afterDiscardingNewEditsAreKeptAsUsual() {
		add("a");
		manager.discardAllEdits();
		add("b");

		fixture.undo();
		fixture.undo();

		assertEquals("undo b", log());
	}

	@Test
	public void undoingWithoutAnEditIsAnError() {
		assertThrows(TGCannotUndoException.class, () -> manager.undo(fixture.actionContext));
	}

	@Test
	public void redoingWithoutAnEditIsAnError() {
		add("a");

		assertThrows(TGCannotRedoException.class, () -> manager.redo(fixture.actionContext));
	}

	@Test
	public void aFailingUndoFailsTheActionAndTheEditStaysToBeUndone() {
		RecordingEdit edit = add("a");
		edit.failing = true;

		assertThrows(TGActionException.class, fixture::undo);
		edit.failing = false;
		fixture.undo();

		assertEquals("undo a", log());
	}

	@Test
	public void aFailingRedoFailsTheActionAndTheEditStaysToBeRedone() {
		RecordingEdit edit = add("a");
		fixture.undo();
		edit.failing = true;

		assertThrows(TGActionException.class, fixture::redo);
		edit.failing = false;
		fixture.redo();

		assertEquals("undo a, redo a", log());
	}
}
