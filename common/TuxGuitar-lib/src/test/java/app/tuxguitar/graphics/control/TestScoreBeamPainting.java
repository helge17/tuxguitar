package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how the stems of a beam group paint its beams (TGVoiceImpl.paintScoreBeat, after the stem): one beam per
 * level a note's value adds (eighth: 1, sixteenth: 2, thirty-second: 3), each 4 thick, 6 further inward per level.
 * A voice draws each of its levels from the first to the last stem it is joined with, except where a neighbour
 * draws it: a previous voice beamed to it draws the levels both have up to its stem, and so does a longer next
 * voice beamed back to it. A level only one note has, beside a longer note, is a stub: 7 units right of a first
 * note, 6 left of a last one. All of it scales, and moves with the beats when the line widens the measure.
 *
 * Notes are on the high E string, one after another (from the measure start unless the test moves them), beamed
 * in one group. Each voice paints its score beat alone at (0, 0); the result lists what each voice draws after its
 * stem, separated by "--".
 */
public class TestScoreBeamPainting {

	private final LayoutHarness harness = new LayoutHarness();

	// the extra width the line gives the measure, spread over its beats (0: the measure keeps its own width)
	private float measureSpacing = 0f;

	// where the notes start, from the measure start
	private long start = 0;

	// two eighths, stems down: the first draws the beam to the second, which draws nothing
	private static final String TWO_EIGHTHS = ""
		+ "initPath 2\n"
		+ "moveTo 80.5 18.125\n"
		+ "lineTo 80.5 22.125\n"
		+ "lineTo 99.5 22.125\n"
		+ "lineTo 99.5 18.125\n"
		+ "lineTo 80.5 18.125\n"
		+ "closePath\n"
		+ "--\n";

	@Test
	public void twoEighthsShareOneBeamDrawnByTheFirst() {
		assertEquals(TWO_EIGHTHS, beams(TGVoice.DIRECTION_DOWN, 8, 8));
	}

	@Test
	public void aGroupEndingTheMeasureIsBeamedAlike() {
		harness.quarterNotes(0, 1, 2);
		start = 3 * LayoutHarness.QUARTER;

		assertEquals(""
			+ "initPath 2\n"
			+ "moveTo 188.5 18.125\n"
			+ "lineTo 188.5 22.125\n"
			+ "lineTo 207.5 22.125\n"
			+ "lineTo 207.5 18.125\n"
			+ "lineTo 188.5 18.125\n"
			+ "closePath\n"
			+ "--\n"
			, beams(TGVoice.DIRECTION_DOWN, 8, 8));
	}

	@Test
	public void withStemsUpTheBeamRunsAboveFromTheRightOfTheNoteHeads() {
		assertEquals(""
			+ "initPath 2\n"
			+ "moveTo 88.095 -37.0\n"
			+ "lineTo 88.095 -33.0\n"
			+ "lineTo 107.095 -33.0\n"
			+ "lineTo 107.095 -37.0\n"
			+ "lineTo 88.095 -37.0\n"
			+ "closePath\n"
			+ "--\n"
			, beams(TGVoice.DIRECTION_UP, 8, 8));
	}

	@Test
	public void eachShorterValueAddsABeamFurtherInwardAndALastShorterNoteDrawsBackToThePreviousStem() {
		assertEquals(""
			+ "initPath 2\n"
			+ "moveTo 80.5 18.125\n"
			+ "lineTo 80.5 22.125\n"
			+ "lineTo 97.5 22.125\n"
			+ "lineTo 97.5 18.125\n"
			+ "lineTo 80.5 18.125\n"
			+ "moveTo 80.5 12.125\n"
			+ "lineTo 80.5 16.125\n"
			+ "lineTo 97.5 16.125\n"
			+ "lineTo 97.5 12.125\n"
			+ "lineTo 80.5 12.125\n"
			+ "moveTo 80.5 6.125\n"
			+ "lineTo 80.5 10.125\n"
			+ "lineTo 97.5 10.125\n"
			+ "lineTo 97.5 6.125\n"
			+ "lineTo 80.5 6.125\n"
			+ "closePath\n"
			+ "--\n"
			+ "\n"
			+ "--\n"
			+ "initPath 2\n"
			+ "moveTo 96.5 18.125\n"
			+ "lineTo 96.5 22.125\n"
			+ "lineTo 113.5 22.125\n"
			+ "lineTo 113.5 18.125\n"
			+ "lineTo 96.5 18.125\n"
			+ "moveTo 96.5 12.125\n"
			+ "lineTo 96.5 16.125\n"
			+ "lineTo 113.5 16.125\n"
			+ "lineTo 113.5 12.125\n"
			+ "lineTo 96.5 12.125\n"
			+ "closePath\n"
			+ "--\n"
			+ "initPath 2\n"
			+ "moveTo 112.5 18.125\n"
			+ "lineTo 112.5 22.125\n"
			+ "lineTo 145.5 22.125\n"
			+ "lineTo 145.5 18.125\n"
			+ "lineTo 112.5 18.125\n"
			+ "closePath"
			, beams(TGVoice.DIRECTION_DOWN, 32, 32, 16, 8));
	}

	@Test
	public void anEighthBeforeSixteenthsBeamsOnlyToTheFirstSixteenth() {
		assertEquals(""
			+ "initPath 2\n"
			+ "moveTo 80.5 18.125\n"
			+ "lineTo 80.5 22.125\n"
			+ "lineTo 113.5 22.125\n"
			+ "lineTo 113.5 18.125\n"
			+ "lineTo 80.5 18.125\n"
			+ "closePath\n"
			+ "--\n"
			+ "initPath 2\n"
			+ "moveTo 112.5 18.125\n"
			+ "lineTo 112.5 22.125\n"
			+ "lineTo 129.5 22.125\n"
			+ "lineTo 129.5 18.125\n"
			+ "lineTo 112.5 18.125\n"
			+ "moveTo 112.5 12.125\n"
			+ "lineTo 112.5 16.125\n"
			+ "lineTo 129.5 16.125\n"
			+ "lineTo 129.5 12.125\n"
			+ "lineTo 112.5 12.125\n"
			+ "closePath\n"
			+ "--\n"
			, beams(TGVoice.DIRECTION_DOWN, 8, 16, 16));
	}

	@Test
	public void aSixteenthBesideOnlyAnEighthGetsAStubTowardsIt() {
		assertEquals(""
			+ "initPath 2\n"
			+ "moveTo 80.5 12.125\n"
			+ "lineTo 80.5 16.125\n"
			+ "lineTo 87.5 16.125\n"
			+ "lineTo 87.5 12.125\n"
			+ "lineTo 80.5 12.125\n"
			+ "closePath\n"
			+ "--\n"
			+ "initPath 2\n"
			+ "moveTo 80.5 18.125\n"
			+ "lineTo 80.5 22.125\n"
			+ "lineTo 129.5 22.125\n"
			+ "lineTo 129.5 18.125\n"
			+ "lineTo 80.5 18.125\n"
			+ "closePath\n"
			+ "--\n"
			+ "initPath 2\n"
			+ "moveTo 123.5 12.125\n"
			+ "lineTo 123.5 16.125\n"
			+ "lineTo 129.5 16.125\n"
			+ "lineTo 129.5 12.125\n"
			+ "lineTo 123.5 12.125\n"
			+ "closePath"
			, beams(TGVoice.DIRECTION_DOWN, 16, 8, 16));
	}

	@Test
	public void stubsAndBeamsScaleAndFollowTheBeatsSpreadOverAWiderMeasure() {
		harness.scale = 2f;
		measureSpacing = 40f;

		assertEquals(""
			+ "initPath 2\n"
			+ "moveTo 161.0 24.25\n"
			+ "lineTo 161.0 32.25\n"
			+ "lineTo 175.0 32.25\n"
			+ "lineTo 175.0 24.25\n"
			+ "lineTo 161.0 24.25\n"
			+ "closePath\n"
			+ "--\n"
			+ "initPath 2\n"
			+ "moveTo 161.0 36.25\n"
			+ "lineTo 161.0 44.25\n"
			+ "lineTo 266.5 44.25\n"
			+ "lineTo 266.5 36.25\n"
			+ "lineTo 161.0 36.25\n"
			+ "closePath\n"
			+ "--\n"
			+ "initPath 2\n"
			+ "moveTo 254.5 24.25\n"
			+ "lineTo 254.5 32.25\n"
			+ "lineTo 266.5 32.25\n"
			+ "lineTo 266.5 24.25\n"
			+ "lineTo 254.5 24.25\n"
			+ "closePath"
			, beams(TGVoice.DIRECTION_DOWN, 16, 8, 16));
	}

	// ---- helpers ----

	// lays out notes of the given values from the start offset, stems in the given direction, then what each one
	// draws after its stem
	private String beams(int direction, int... durationValues) {
		List<TGVoiceImpl> voices = new ArrayList<TGVoiceImpl>();
		long offset = start;
		for (int durationValue : durationValues) {
			TGVoiceImpl voice = harness.note(offset, durationValue, 0, 1, 5).getVoiceImpl();
			voice.setDirection(direction);
			offset += voice.getDuration().getPreciseTime();
			voices.add(voice);
		}
		TGLayout layout = harness.update(SCORE);
		harness.measure().setSpacing(measureSpacing);
		List<String> beams = new ArrayList<String>();
		for (TGVoiceImpl voice : voices) {
			beams.add(afterStem(layout, voice));
		}
		return String.join("\n--\n", beams);
	}

	// the voice paints its score beat: the footer style (2 calls) and the stem (8 calls), then its beams
	private String afterStem(TGLayout layout, TGVoiceImpl voice) {
		harness.painter.clear();
		voice.paintScoreBeat(layout, harness.painter, 0, 0, voice.getBeatImpl().getSpacing(layout));
		List<String> calls = harness.painter.calls();
		return String.join("\n", calls.subList(10, calls.size()));
	}
}
