package app.tuxguitar.editor.action.measure;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.editor.action.SongActionFixture;

/**
 * Pins TGAddMeasureListAction: the count of new measures go in at the measure number, which can be one past the
 * song end; a number out of the song, or a count of zero, adds nothing.
 *
 * The song starts with one measure holding one note; it reads back as the notes of each measure, a new measure
 * holding only a rest.
 */
public class TestAddMeasureListAction {

	private final SongActionFixture fixture = new SongActionFixture();

	public TestAddMeasureListAction() {
		fixture.note(fixture.beat(fixture.song.getTrack(0).getMeasure(0), 0), 1, 1);
		TGActionManager.getInstance(fixture.context).mapAction(TGAddMeasureAction.NAME, new TGAddMeasureAction(fixture.context));
	}

	private String add(int number, int count) {
		fixture.actionContext.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, number);
		fixture.actionContext.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, count);
		fixture.execute(new TGAddMeasureListAction(fixture.context));
		return fixture.measures(measure -> SongActionFixture.describe(measure.getBeat(0)));
	}

	@TestFactory
	public List<DynamicTest> addingMeasures() {
		return Arrays.asList(
			adding("the measures go in before the measure number", " |  | s1f1", 1, 2),
			adding("the measures can go after the song end", "s1f1 |  | ", 2, 2),
			adding("a number past the song end adds nothing", "s1f1", 3, 2),
			adding("a number before the first measure adds nothing", "s1f1", 0, 2),
			adding("a count of zero adds nothing", "s1f1", 1, 0));
	}

	private static DynamicTest adding(String name, String expected, int number, int count) {
		return reads(name, expected, () -> new TestAddMeasureListAction().add(number, count));
	}
}
