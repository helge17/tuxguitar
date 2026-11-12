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
 * stem (one path of beams, or nothing), separated by "--".
 */
public class TestScoreBeamPainting {

	private final LayoutHarness harness = new LayoutHarness();

	// the extra width the line gives the measure, spread over its beats (0: the measure keeps its own width)
	private float measureSpacing = 0f;

	// where the notes start, from the measure start
	private long start = 0;

	@Test
	public void twoEighthsShareOneBeamDrawnByTheFirst() {
		assertEquals(voices(
			path(beam(80.5f, 99.5f, 18.125f, 22.125f)),
			""
			), beams(TGVoice.DIRECTION_DOWN, 8, 8));
	}

	@Test
	public void aGroupEndingTheMeasureIsBeamedAlike() {
		harness.quarterNotes(0, 1, 2);
		start = 3 * LayoutHarness.QUARTER;

		assertEquals(voices(
			path(beam(188.5f, 207.5f, 18.125f, 22.125f)),
			""
			), beams(TGVoice.DIRECTION_DOWN, 8, 8));
	}

	@Test
	public void withStemsUpTheBeamRunsAboveFromTheRightOfTheNoteHeads() {
		assertEquals(voices(
			path(beam(88.095f, 107.095f, -37f, -33f)),
			""
			), beams(TGVoice.DIRECTION_UP, 8, 8));
	}

	@Test
	public void eachShorterValueAddsABeamFurtherInwardAndALastShorterNoteDrawsBackToThePreviousStem() {
		assertEquals(voices(
			path(beam(80.5f, 97.5f, 18.125f, 22.125f), beam(80.5f, 97.5f, 12.125f, 16.125f), beam(80.5f, 97.5f, 6.125f, 10.125f)),
			"",
			path(beam(96.5f, 113.5f, 18.125f, 22.125f), beam(96.5f, 113.5f, 12.125f, 16.125f)),
			path(beam(112.5f, 145.5f, 18.125f, 22.125f))
			), beams(TGVoice.DIRECTION_DOWN, 32, 32, 16, 8));
	}

	@Test
	public void anEighthBeforeSixteenthsBeamsOnlyToTheFirstSixteenth() {
		assertEquals(voices(
			path(beam(80.5f, 113.5f, 18.125f, 22.125f)),
			path(beam(112.5f, 129.5f, 18.125f, 22.125f), beam(112.5f, 129.5f, 12.125f, 16.125f)),
			""
			), beams(TGVoice.DIRECTION_DOWN, 8, 16, 16));
	}

	@Test
	public void aSixteenthBesideOnlyAnEighthGetsAStubTowardsIt() {
		assertEquals(voices(
			path(beam(80.5f, 87.5f, 12.125f, 16.125f)),
			path(beam(80.5f, 129.5f, 18.125f, 22.125f)),
			path(beam(123.5f, 129.5f, 12.125f, 16.125f))
			), beams(TGVoice.DIRECTION_DOWN, 16, 8, 16));
	}

	@Test
	public void stubsAndBeamsScaleAndFollowTheBeatsSpreadOverAWiderMeasure() {
		harness.quarterNotes(0);
		start = LayoutHarness.QUARTER;
		harness.scale = 2f;
		measureSpacing = 40f;

		assertEquals(voices(
			path(beam(299f, 313f, 24.25f, 32.25f)),
			path(beam(299f, 404.5f, 36.25f, 44.25f)),
			path(beam(392.5f, 404.5f, 24.25f, 32.25f))
			), beams(TGVoice.DIRECTION_DOWN, 16, 8, 16));
	}

	// ---- expected drawings ----

	private static String voices(String... drawn) {
		return String.join("\n--\n", drawn);
	}

	// one filled path holding the given beams
	private static String path(String... beams) {
		return "initPath 2\n" + String.join("\n", beams) + "\nclosePath";
	}

	// the outline of a flat beam from x1 to x2, between the top and bottom y
	private static String beam(float x1, float x2, float top, float bottom) {
		return "moveTo " + x1 + " " + top + "\n"
			+ "lineTo " + x1 + " " + bottom + "\n"
			+ "lineTo " + x2 + " " + bottom + "\n"
			+ "lineTo " + x2 + " " + top + "\n"
			+ "lineTo " + x1 + " " + top;
	}

	// ---- painting ----

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
		return voices(beams.toArray(new String[0]));
	}

	// the voice paints its score beat: the footer style (2 calls) and the stem (8 calls), then its beams
	private String afterStem(TGLayout layout, TGVoiceImpl voice) {
		harness.painter.clear();
		voice.paintScoreBeat(layout, harness.painter, 0, 0, voice.getBeatImpl().getSpacing(layout));
		List<String> calls = harness.painter.calls();
		return String.join("\n", calls.subList(10, calls.size()));
	}
}
