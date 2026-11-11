package app.tuxguitar.editor.action.measure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGRemoveMeasureRangeAction: the measures from the first to the second measure number are removed, when both
 * are in the song, in order, and do not cover the whole song. Otherwise the song is left alone.
 *
 * The song has four measures; measure m holds one beat with a note of fret m on string 1. The song reads back as its
 * measures separated by " | ", then "success" when the measure removal reported it.
 */
public class TestRemoveMeasureRangeAction {

	private static final String UNCHANGED = "s1f1 | s1f2 | s1f3 | s1f4";

	private final SongActionFixture fixture = new SongActionFixture();

	public TestRemoveMeasureRangeAction() {
		for (int m = 1; m < 4; m++) {
			fixture.songManager.addNewMeasureBeforeEnd(fixture.song);
		}
		for (int m = 0; m < 4; m++) {
			fixture.note(fixture.beat(fixture.song.getTrack(0).getMeasure(m), 0), 1, m + 1);
		}
		TGActionManager.getInstance(fixture.context).mapAction(TGRemoveMeasureAction.NAME, new TGRemoveMeasureAction(fixture.context));
	}

	private String removeRange(int measure1, int measure2) {
		fixture.actionContext.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		fixture.actionContext.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		fixture.execute(new TGRemoveMeasureRangeAction(fixture.context));
		TGTrack track = fixture.song.getTrack(0);
		TGBeat[] firstBeats = new TGBeat[track.countMeasures()];
		for (int m = 0; m < firstBeats.length; m++) {
			firstBeats[m] = track.getMeasure(m).getBeat(0);
		}
		return fixture.outcome(firstBeats);
	}

	@TestFactory
	public List<DynamicTest> removingARange() {
		return Arrays.asList(
			removing("the measures in the range are removed", 2, 3, "s1f1 | s1f4 | success"),
			removing("a range of one measure removes it", 2, 2, "s1f1 | s1f3 | s1f4 | success"),
			removing("a range from the first measure keeps the rest", 1, 3, "s1f4 | success"),
			removing("a range to the last measure keeps the start", 2, 4, "s1f1 | success"),
			removing("the whole song is not removed", 1, 4, UNCHANGED),
			removing("a range from measure zero is ignored", 0, 2, UNCHANGED),
			removing("a reversed range is ignored", 3, 2, UNCHANGED),
			removing("a range past the song end is ignored", 3, 5, UNCHANGED));
	}

	// a case run on a new song: removing the range gives the song read back
	private static DynamicTest removing(String name, int measure1, int measure2, String expected) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(expected, new TestRemoveMeasureRangeAction().removeRange(measure1, measure2)));
	}
}
