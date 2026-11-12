package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how a voice paints its rhythm under the tablature when the score is hidden (TGVoiceImpl.paintBeat /
 * paintTablatureBeat): a stem below the strings (above them for stems up), half as long for a half note, none for
 * a whole note; one beam per duration shorter than a quarter, spanning the beamed notes, or a short flag to the
 * right (to the left after a beamed note it cannot join); a dot or two beside the stem end, or under the beams.
 * All of it scales. The voice reserves room for its stem below (or above) the strings.
 *
 * Notes are on string 3 of a 4/4 measure; once printed, the voice under test is painted alone at (0, 0).
 */
public class TestTablatureBeatPainting {

	private static final long SIXTEENTH = QUARTER / 4;

	private final LayoutHarness harness = new LayoutHarness();

	@Test
	public void aQuarterNoteHasAStemUnderTheStringsAndAHalfNoteOneHalfAsLong() {
		TGVoiceImpl quarter = note(0, TGDuration.QUARTER);
		TGVoiceImpl half = note(QUARTER, TGDuration.HALF);

		assertEquals(""
			+ stemStart("initPath 3")
			+ "moveTo 47.0 64.0\n"
			+ "lineTo 47.0 80.0\n"
			+ "closePath\n--\n"
			+ stemStart("initPath")
			+ "moveTo 67.0 72.0\n"
			+ "lineTo 67.0 80.0\n"
			+ "closePath"
			, paint(quarter) + "\n--\n" + paint(half));
	}

	@Test
	public void aWholeNoteHasNoStem() {
		TGVoiceImpl voice = note(0, TGDuration.WHOLE);

		assertEquals("", paint(voice));
	}

	@Test
	public void beamedEighthsShareABeam() {
		TGVoiceImpl first = note(0, TGDuration.EIGHTH);
		TGVoiceImpl second = note(2 * SIXTEENTH, TGDuration.EIGHTH);

		assertEquals(""
			+ stemStart("initPath 3")
			+ "moveTo 47.0 64.0\n"
			+ "lineTo 47.0 80.0\n"
			+ beam(47, 65, 80, 82)
			+ "closePath\n"
			+ "--\n"
			+ stemStart("initPath 3")
			+ "moveTo 65.0 64.0\n"
			+ "lineTo 65.0 80.0\n"
			+ beam(47, 65, 80, 82)
			+ "closePath"
			, paint(first) + "\n--\n" + paint(second));
	}

	@Test
	public void aLoneEighthHasAFlagToTheRightAndASixteenthAfterAnEighthTwoFlagsToTheLeft() {
		TGVoiceImpl lone = note(3 * QUARTER, TGDuration.EIGHTH);
		note(0, TGDuration.EIGHTH);
		TGVoiceImpl afterEighth = note(2 * SIXTEENTH, TGDuration.SIXTEENTH);

		assertEquals(""
			+ stemStart("initPath 3")
			+ "moveTo 239.0 64.0\n"
			+ "lineTo 239.0 80.0\n"
			+ beam(239, 245, 80, 82)
			+ "closePath\n"
			+ "--\n"
			+ stemStart("initPath 3")
			+ "moveTo 79.0 64.0\n"
			+ "lineTo 79.0 80.0\n"
			+ beam(74, 79, 76, 78)
			+ beam(74, 79, 80, 82)
			+ "closePath"
			, paint(lone) + "\n--\n" + paint(afterEighth));
	}

	@Test
	public void stemsUpAreAboveTheStrings() {
		TGVoiceImpl voice = note(0, TGDuration.EIGHTH);
		voice.setDirection(TGVoice.DIRECTION_UP);

		assertEquals(""
			+ stemStart("initPath 3")
			+ "moveTo 47.0 15.0\n"
			+ "lineTo 47.0 1.0\n"
			+ beam(47, 53, 1, -1)
			+ "closePath"
			, paint(voice));
	}

	@Test
	public void aDotSitsBesideTheStemEndOrUnderTheBeams() {
		TGVoiceImpl quarter = note(0, TGDuration.QUARTER);
		quarter.getDuration().setDotted(true);
		TGVoiceImpl beamed = note(QUARTER, TGDuration.EIGHTH);
		beamed.getDuration().setDotted(true);
		note(QUARTER + 3 * SIXTEENTH, TGDuration.SIXTEENTH);

		assertEquals("lineTo 47.0 80.0 | addCircle 51.0 79.0 3.0 / lineTo 111.0 80.0 | addCircle 115.0 76.0 3.0", dots(quarter) + " / " + dots(beamed));
	}

	@Test
	public void theDotOfALastBeamedNoteSitsLeftOfItsStem() {
		note(0, TGDuration.SIXTEENTH);
		TGVoiceImpl last = note(SIXTEENTH, TGDuration.EIGHTH);
		last.getDuration().setDotted(true);

		assertEquals("lineTo 63.0 80.0 | addCircle 58.0 76.0 3.0", dots(last));
	}

	@Test
	public void aDoubleDottedWholeNoteHasTwoDotsWhereItsStemWouldEnd() {
		TGVoiceImpl whole = note(0, TGDuration.WHOLE);
		whole.getDuration().setDoubleDotted(true);

		assertEquals("no stem | addCircle 47.0 79.0 3.0 addCircle 52.0 79.0 3.0", dots(whole));
	}

	@Test
	public void onTheTablatureAloneAVoiceReservesRoomForItsStem() {
		TGVoiceImpl down = note(0, TGDuration.QUARTER);
		TGVoiceImpl up = note(QUARTER, TGDuration.QUARTER);
		up.setDirection(TGVoice.DIRECTION_UP);
		harness.update(TAB);
		String tablature = extent(down) + " " + extent(up);
		harness.update(SCORE | TAB);

		assertEquals("0.0..61.0 -21.0..40.0 | 0.0..0.0 0.0..0.0", tablature + " | " + extent(down) + " " + extent(up));
	}

	@Test
	public void stemsBeamsFlagsAndDotsScaleAndFollowTheBeatsOfAWiderMeasure() {
		TGVoiceImpl beamed = note(0, TGDuration.EIGHTH);
		TGVoiceImpl afterBeamed = note(2 * SIXTEENTH, TGDuration.SIXTEENTH);
		TGVoiceImpl lone = note(QUARTER, TGDuration.EIGHTH);
		lone.getDuration().setDotted(true);
		TGVoiceImpl loneSixteenth = note(2 * QUARTER, TGDuration.SIXTEENTH);
		loneSixteenth.getDuration().setDotted(true);
		note(3 * QUARTER, TGDuration.SIXTEENTH);
		TGVoiceImpl beamedBack = note(3 * QUARTER + SIXTEENTH, TGDuration.EIGHTH);
		beamedBack.getDuration().setDotted(true);
		harness.scale = 2f;
		TGLayout layout = harness.update(TAB);
		harness.measure().setSpacing(40f);

		assertEquals(""
			// an eighth beamed to the sixteenth after it
			+ "initPath 3; setAntialias false; setLineWidth 2.0; moveTo 194.0 88.0; lineTo 194.0 120.0; moveTo 194.0 120.0; lineTo 327.0 120.0; lineTo 327.0 124.0; lineTo 194.0 124.0; lineTo 194.0 120.0; closePath\n--\n"
			// that sixteenth: two flags to the left
			+ "initPath 3; setAntialias false; setLineWidth 2.0; moveTo 327.0 88.0; lineTo 327.0 120.0; moveTo 317.0 112.0; lineTo 327.0 112.0; lineTo 327.0 116.0; lineTo 317.0 116.0; lineTo 317.0 112.0; moveTo 317.0 120.0; lineTo 327.0 120.0; lineTo 327.0 124.0; lineTo 317.0 124.0; lineTo 317.0 120.0; closePath\n--\n"
			// a lone dotted eighth: a flag right, the dot right of the stem end
			+ "initPath 3; setAntialias false; setLineWidth 2.0; moveTo 460.0 88.0; lineTo 460.0 120.0; moveTo 460.0 120.0; lineTo 472.0 120.0; lineTo 472.0 124.0; lineTo 460.0 124.0; lineTo 460.0 120.0; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 0.0; initPath 2; moveTo 468.0 112.0; addCircle 468.0 112.0 6.0; closePath\n--\n"
			// a lone dotted sixteenth: two flags right, the dot under them
			+ "initPath 3; setAntialias false; setLineWidth 2.0; moveTo 726.0 88.0; lineTo 726.0 120.0; moveTo 726.0 112.0; lineTo 738.0 112.0; lineTo 738.0 116.0; lineTo 726.0 116.0; lineTo 726.0 112.0; moveTo 726.0 120.0; lineTo 738.0 120.0; lineTo 738.0 124.0; lineTo 726.0 124.0; lineTo 726.0 120.0; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 0.0; initPath 2; moveTo 734.0 104.0; addCircle 734.0 104.0 6.0; closePath\n--\n"
			// a dotted eighth beamed back to the sixteenth before it: the dot left of the stem, under the beam
			+ "initPath 3; setAntialias false; setLineWidth 2.0; moveTo 1058.5 88.0; lineTo 1058.5 120.0; moveTo 992.0 120.0; lineTo 1058.5 120.0; lineTo 1058.5 124.0; lineTo 992.0 124.0; lineTo 992.0 120.0; closePath; setForeground rgb(0,0,0); setBackground rgb(0,0,0); setLineWidth 0.0; initPath 2; moveTo 1048.5 112.0; addCircle 1048.5 112.0 6.0; closePath"
			, scaled(layout, beamed) + "\n--\n" + scaled(layout, afterBeamed) + "\n--\n" + scaled(layout, lone) + "\n--\n" + scaled(layout, loneSixteenth) + "\n--\n" + scaled(layout, beamedBack));
	}

	// ---- expected drawings ----

	// the footer style, then the path of the stem (and beams)
	private static String stemStart(String initPath) {
		return ""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ initPath + "\n"
			+ "setAntialias false\n"
			+ "setLineWidth 1.0\n";
	}

	// the outline of a beam from x1 to x2, between y1 and y2
	private static String beam(int x1, int x2, int y1, int y2) {
		return ""
			+ "moveTo " + x1 + ".0 " + y1 + ".0\n"
			+ "lineTo " + x2 + ".0 " + y1 + ".0\n"
			+ "lineTo " + x2 + ".0 " + y2 + ".0\n"
			+ "lineTo " + x1 + ".0 " + y2 + ".0\n"
			+ "lineTo " + x1 + ".0 " + y1 + ".0\n";
	}

	// ---- painting ----

	private TGVoiceImpl note(long offset, int durationValue) {
		return harness.note(offset, durationValue, 0, 3, 5).getVoiceImpl();
	}

	private String paint(TGVoiceImpl voice) {
		harness.print(TAB);
		harness.painter.clear();
		voice.paintBeat(harness.layout(), harness.painter, 0, 0);
		return String.join("\n", harness.painter.calls());
	}

	// the voice paints its tablature beat at (100, 0), its calls without the footer style, "; " separated
	private String scaled(TGLayout layout, TGVoiceImpl voice) {
		harness.painter.clear();
		voice.paintTablatureBeat(layout, harness.painter, 100, 0, voice.getBeatImpl().getSpacing(layout));
		return harness.painter.joined(2, "; ");
	}

	// the end of the stem (its first line), then the dots
	private String dots(TGVoiceImpl voice) {
		paint(voice);
		List<String> lines = harness.painter.callsStartingWith("lineTo");
		return (lines.isEmpty() ? "no stem" : lines.get(0)) + " | " + String.join(" ", harness.painter.callsStartingWith("addCircle"));
	}

	private static String extent(TGVoiceImpl voice) {
		return voice.getMinY() + ".." + voice.getMaxY();
	}
}
