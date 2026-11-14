package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.editor.undo.impl.song.TGUndoableSongGenericController;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableAddMeasureController;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableRemoveMeasureController;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on the song as a whole: making it, and whether the last action succeeded.
 */
public class SongSteps {

	private final AudioWorld world;

	public SongSteps(AudioWorld world) {
		this.world = world;
	}

	@Given("a new song with one guitar track and {int} measures of 4\\/4 at {int} bpm")
	public void aNewSong(int measures, int bpm) {
		world.fixture().measureCount(measures);
		Iterator<TGMeasureHeader> headers = world.song().getMeasureHeaders();
		while (headers.hasNext()) {
			headers.next().getTempo().setQuarterValue(bpm);
		}
	}


	@Given("I have repeated measures {int} to {int} once")
	public void repeatMeasures(int first, int last) {
		world.song().getMeasureHeader(first - 1).setRepeatOpen(true);
		world.song().getMeasureHeader(last - 1).setRepeatClose(1);
	}

	// an alternate ending is a bit per pass: the first ending plays on the first pass, the second on the second
	@Given("I have repeated measure {int} once, with measure {int} as the first ending and measure {int} as the second")
	public void repeatWithAlternateEndings(int repeated, int first, int second) {
		repeatMeasures(repeated, first);
		world.song().getMeasureHeader(first - 1).setRepeatAlternative(1);
		world.song().getMeasureHeader(second - 1).setRepeatAlternative(2);
	}

	@Then("the action succeeds")
	public void theActionSucceeds() {
		assertTrue(world.succeeded());
	}

	@Then("the action is refused")
	public void theActionIsRefused() {
		assertFalse(world.succeeded());
	}

	@Then("the song has {int} channel(s)")
	public void theSongHasChannels(int count) {
		assertEquals(count, world.song().countChannels());
	}

	@Then("the song has {int} measures")
	public void theSongHasMeasures(int count) {
		assertEquals(count, world.song().countMeasureHeaders());
	}

	@When("I undo")
	public void undo() {
		world.fixture().undo();
	}

	@Given("I have undone")
	public void haveUndone() {
		undo();
		assertTrue(TGUndoableManager.getInstance(world.fixture().context).canRedo());
	}

	@When("I undo {int} times")
	public void undoTimes(int times) {
		for (int i = 0; i < times; i++) {
			undo();
		}
	}

	@When("I redo")
	public void redo() {
		world.fixture().redo();
	}

	// a song of one track and that many measures copied over the song, under the controller the app maps to it
	@When("I copy in a song of {int} measures")
	public void copyInASong(int measures) {
		TGSong from = world.fixture().songManager.newSong();
		while (from.countMeasureHeaders() < measures) {
			world.fixture().songManager.addNewMeasureBeforeEnd(from);
		}
		world.fixture().actionContext.setAttribute(TGCopySongFromAction.ATTRIBUTE_FROM, from);
		world.executeUndoable(new TGCopySongFromAction(world.fixture().context), new TGUndoableSongGenericController());
	}

	@When("I insert a measure before measure {int}")
	public void insertAMeasure(int number) {
		world.fixture().actionContext.setAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER, number);
		world.executeUndoable(new TGAddMeasureAction(world.fixture().context), new TGUndoableAddMeasureController());
	}

	@Given("I have inserted a measure before measure {int}")
	public void haveInsertedAMeasure(int number) {
		int count = world.song().countMeasureHeaders();
		insertAMeasure(number);
		assertEquals(count + 1, world.song().countMeasureHeaders());
	}

	@When("I remove measure {int}")
	public void removeMeasure(int number) {
		world.fixture().actionContext.setAttribute(TGRemoveMeasureAction.ATTRIBUTE_MEASURE_NUMBER, number);
		world.executeUndoable(new TGRemoveMeasureAction(world.fixture().context), new TGUndoableRemoveMeasureController());
	}

	@Given("I have removed measure {int}")
	public void haveRemovedMeasure(int number) {
		removeMeasure(number);
		assertTrue(world.succeeded());
	}

	// the app runs the range without an undoable controller of its own (desktop's TGActionConfigMap)
	@When("I remove measures {int} to {int}")
	public void removeMeasures(int first, int last) {
		world.fixture().actionContext.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_1, first);
		world.fixture().actionContext.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_2, last);
		world.execute(new TGRemoveMeasureRangeAction(world.fixture().context));
	}
}
