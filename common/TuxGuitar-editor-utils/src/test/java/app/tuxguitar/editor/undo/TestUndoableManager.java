package app.tuxguitar.editor.undo;

import static app.tuxguitar.editor.action.SongActionFixture.reads;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.action.TGActionException;
import app.tuxguitar.editor.action.SongActionFixture;

/**
 * Pins TGUndoableManager, driven as the app drives it: edits are added as actions run, and the undo and redo actions
 * undo the last edit done and redo the first edit undone, when the edit says it can. A new edit drops the edits
 * undone; only the last 100 edits are kept. Undoing or redoing without an edit, or with an edit that fails, is an
 * error, and the failing edit stays where it was.
 *
 * A script of steps is run: "+a" adds the edit a, "u" runs the undo action, "r" the redo action, "x" discards all
 * edits. Each edit writes what it does to the log, read back as "undo a, redo a, ...".
 */
public class TestUndoableManager {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGUndoableManager manager = TGUndoableManager.getInstance(fixture.context);
	private final List<String> log = new ArrayList<String>();

	private RecordingEdit add(String name) {
		return RecordingEdit.addedTo(manager::addEdit, name, log);
	}

	private String run(String script) {
		for (String step : script.split(" ")) {
			if (step.startsWith("+")) {
				add(step.substring(1));
			} else if (step.equals("u")) {
				fixture.undo();
			} else if (step.equals("r")) {
				fixture.redo();
			} else {
				manager.discardAllEdits();
			}
		}
		return String.join(", ", log);
	}

	@TestFactory
	public List<DynamicTest> undoingAndRedoing() {
		return Arrays.asList(
			running("undo goes back from the last edit, redo forward again", "+a +b u u r r", "undo b, undo a, redo a, redo b"),
			running("undo and redo stop at the ends", "+a r u u r r", "undo a, redo a"),
			running("a new edit drops the edits undone", "+a +b u +c r u u", "undo b, undo c, undo a"),
			running("after discarding, new edits are kept as usual", "+a x +b u u", "undo b"));
	}

	private static DynamicTest running(String name, String script, String expected) {
		return reads(name, expected, () -> new TestUndoableManager().run(script));
	}

	@Test
	public void anEditThatCannotBeUndoneIsLeftAsIs() {
		add("a");
		add("b").undoable = false;

		assertEquals("false / ", manager.canUndo() + " / " + run("u"));
	}

	@Test
	public void anEditThatCannotBeRedoneIsLeftAsIs() {
		add("a").redoable = false;

		String undone = run("u r");

		assertEquals("false / undo a", manager.canRedo() + " / " + undone);
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
		run("+a x");

		assertEquals("false false", manager.canUndo() + " " + manager.canRedo());
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

	// the edit fails the step, which fails the action; once mended, the same step reaches it again
	private String failThenMend(RecordingEdit edit, Runnable step) {
		edit.failing = true;
		assertThrows(TGActionException.class, step::run);
		edit.failing = false;
		step.run();
		return String.join(", ", log);
	}

	@Test
	public void aFailingEditFailsTheActionAndStaysWhereItWas() {
		RecordingEdit edit = add("a");

		failThenMend(edit, fixture::undo);

		assertEquals("undo a, redo a", failThenMend(edit, fixture::redo));
	}
}
