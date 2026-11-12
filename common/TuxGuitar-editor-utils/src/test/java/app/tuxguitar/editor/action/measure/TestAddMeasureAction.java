package app.tuxguitar.editor.action.measure;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.editor.action.SongActionFixture;

/**
 * TGAddMeasureAction puts a new measure in at the measure number, which can be one past the song end, and reports
 * success; a number out of the song adds nothing and reports nothing, so a refused insert can be told from a done one.
 *
 * The song starts with one measure holding one note; it reads back as the notes of each measure (a new measure holding
 * only a rest), then "success" when the action reported it.
 */
public class TestAddMeasureAction {

	private final SongActionFixture fixture = new SongActionFixture();

	public TestAddMeasureAction() {
		fixture.note(fixture.beat(fixture.song.getTrack(0).getMeasure(0), 0), 1, 1);
	}

	private String add(int number) {
		fixture.actionContext.setAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER, number);
		fixture.execute(new TGAddMeasureAction(fixture.context));
		String measures = fixture.measures(measure -> SongActionFixture.describe(measure.getBeat(0)));
		return fixture.outcome().isEmpty() ? measures : measures + " | " + fixture.outcome();
	}

	@TestFactory
	public List<DynamicTest> addingAMeasure() {
		return Arrays.asList(
			adding("the measure goes in before the measure number", " | s1f1 | success", 1),
			adding("the measure can go after the song end", "s1f1 |  | success", 2),
			adding("a number past the song end adds nothing", "s1f1", 3),
			adding("a number before the first measure adds nothing", "s1f1", 0));
	}

	private static DynamicTest adding(String name, String expected, int number) {
		return reads(name, expected, () -> new TestAddMeasureAction().add(number));
	}
}
