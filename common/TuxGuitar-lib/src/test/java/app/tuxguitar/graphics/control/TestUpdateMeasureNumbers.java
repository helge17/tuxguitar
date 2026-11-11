package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;

/**
 * Pins which measures the layout lays out again when measures change (TGLayout.updateMeasureNumbers): the measures
 * of the given numbers, numbers beyond the song ignored, plus once the measures that asked to follow one of them
 * (a closed hi-hat opening a measure is drawn after looking at the end of the previous measure).
 *
 * The song has 3 measures, laid out before notes are added to them; the layout of each measure is read back as its
 * count of beats that are not rests, in measure order.
 */
public class TestUpdateMeasureNumbers {

	private final LayoutHarness harness = new LayoutHarness();

	public TestUpdateMeasureNumbers() {
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
	}

	// a quarter of the drum (or fret) at the given quarter of every given measure, after the song was laid out
	private void addQuarters(int quarter, int value, int... measureIndexes) {
		for (int measureIndex : measureIndexes) {
			harness.note(measureIndex, quarter * QUARTER, TGDuration.QUARTER, 0, 1, value);
		}
	}

	private String update(Integer... numbers) {
		harness.layout().updateMeasureNumbers(Arrays.asList(numbers));
		StringBuilder layout = new StringBuilder();
		for (int i = 0; i < harness.song.countMeasureHeaders(); i++) {
			layout.append(layout.length() == 0 ? "" : " ").append(harness.measure(i).getNotEmptyBeats());
		}
		return layout.toString();
	}

	@Test
	public void laysOutOnlyTheGivenMeasures() {
		harness.update(SCORE);
		addQuarters(0, 5, 0, 1, 2);

		assertEquals("1 0 1", update(3, 1));
	}

	@Test
	public void ignoresNumbersBeyondTheSong() {
		harness.update(SCORE);
		addQuarters(0, 5, 0, 1, 2);

		assertEquals("0 1 0", update(4, 2, 0));
	}

	@Test
	public void alsoLaysOutOnceTheMeasuresFollowingAGivenOne() {
		harness.percussion();
		addQuarters(3, 42, 0);
		addQuarters(0, 42, 1);
		harness.print(SCORE);
		addQuarters(1, 42, 1);
		String first = update(1);
		addQuarters(2, 42, 1);

		assertEquals("1 2 0 | 1 2 0", first + " | " + update(1));
	}

	@Test
	public void aFollowingMeasureRemovedFromTheSongIsSkipped() {
		harness.percussion();
		addQuarters(3, 42, 0);
		addQuarters(0, 42, 1);
		harness.print(SCORE);
		harness.songManager.removeMeasureHeader(harness.song, 3);
		harness.songManager.removeMeasureHeader(harness.song, 2);
		addQuarters(2, 42, 0);

		assertEquals("2", update(1));
	}
}
