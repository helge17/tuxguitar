package app.tuxguitar.editor.action.measure;

import static app.tuxguitar.editor.action.SongActionFixture.reads;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGMeasure;

/**
 * Pins TGCleanMeasureListAction: the caret track measures from the first measure number to the second lose their
 * beats; numbers past the track end are skipped, and a first number of zero or past the second cleans nothing.
 * The caret measure and header are given back to the context afterwards.
 *
 * The track has three measures, each holding one beat, and the caret is on the first; it reads back as the count of
 * beats in each measure.
 */
public class TestCleanMeasureListAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure caretMeasure;

	public TestCleanMeasureListAction() {
		fixture.measureCount(3);
		for (int m = 0; m < 3; m++) {
			fixture.beat(fixture.song.getTrack(0).getMeasure(m), 0);
		}
		caretMeasure = fixture.song.getTrack(0).getMeasure(0);
		fixture.caret(caretMeasure.getBeat(0), 1);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, caretMeasure.getHeader());
		TGActionManager.getInstance(fixture.context).mapAction(TGCleanMeasureAction.NAME, new TGCleanMeasureAction(fixture.context));
	}

	private String clean(int number1, int number2) {
		fixture.actionContext.setAttribute(TGCleanMeasureListAction.ATTRIBUTE_MEASURE_NUMBER_1, number1);
		fixture.actionContext.setAttribute(TGCleanMeasureListAction.ATTRIBUTE_MEASURE_NUMBER_2, number2);
		fixture.execute(new TGCleanMeasureListAction(fixture.context));
		return fixture.measures(measure -> String.valueOf(measure.countBeats()));
	}

	@TestFactory
	public List<DynamicTest> cleaningMeasures() {
		return Arrays.asList(
			cleaning("the measures between the numbers are cleaned", "1 | 0 | 0", 2, 3),
			cleaning("a single measure can be cleaned", "1 | 0 | 1", 2, 2),
			cleaning("numbers past the track end are skipped", "1 | 1 | 0", 3, 5),
			cleaning("a first number of zero cleans nothing", "1 | 1 | 1", 0, 2),
			cleaning("a first number past the second cleans nothing", "1 | 1 | 1", 3, 2));
	}

	private static DynamicTest cleaning(String name, String expected, int number1, int number2) {
		return reads(name, expected, () -> new TestCleanMeasureListAction().clean(number1, number2));
	}

	@Test
	public void theCaretMeasureAndHeaderAreGivenBack() {
		clean(2, 3);

		assertSame(caretMeasure, fixture.actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		assertSame(caretMeasure.getHeader(), fixture.actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
	}
}
