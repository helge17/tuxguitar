package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins where a rest is drawn (TGVoiceImpl.updateSilenceSpacing), read back as the voice's vertical
 * extent "minY..maxY" (top of the rest .. top + its height), relative to the staff (score: 5 lines
 * 7 apart; tablature: 6 strings 8 apart).
 */
public class TestSilenceSpacing {

	private static final int[] DURATIONS = {
		TGDuration.WHOLE, TGDuration.HALF, TGDuration.QUARTER, TGDuration.EIGHTH,
		TGDuration.SIXTEENTH, TGDuration.THIRTY_SECOND, TGDuration.SIXTY_FOURTH,
	};

	@Test
	public void aLoneRestIsPlacedByItsDuration() {
		assertEquals(""
			+ "score: 1:7.0..9.839945 2:11.160055..14.0 4:7.7777777..20.222221 8:9.333333..18.666668 16:7.7777777..20.222221 32:4.666666..23.333334 64:3.1111107..24.88889\n"
			+ "tablature: 1:16.0..18.921087 2:21.078913..24.0 4:13.6..26.400002 8:15.2..24.8 16:13.6..26.400002 32:10.4..29.6 64:8.8..31.2"
			, "score: " + restByDuration(SCORE | TAB) + "\ntablature: " + restByDuration(TAB));
	}

	@Test
	public void aLoneWholeRestOnASevenStringTablatureHangsFromTheMiddleString() {
		LayoutHarness harness = new LayoutHarness();
		harness.song.getTrack(0).setStrings(harness.songManager.createDefaultInstrumentStrings(7));
		harness.rest(0, TGDuration.WHOLE, 0);
		harness.update(TAB);

		assertEquals("24.0..26.921087", extent(harness.measure().getBeat(0).getVoice(0)));
	}

	@Test
	public void aHiddenRestTakesNoSpace() {
		LayoutHarness harness = new LayoutHarness();
		harness.note(0, TGDuration.HALF, 0, 3, 5);
		TGVoice rest = harness.rest(0, TGDuration.HALF, 1);
		harness.update(SCORE | TAB);

		assertEquals("0.0..0.0", extent(rest));
	}

	@Test
	public void twoVisibleRestsOnABeatAreStackedByVoice() {
		LayoutHarness harness = new LayoutHarness();
		TGVoice first = harness.rest(0, TGDuration.HALF, 0);
		TGVoice second = harness.rest(0, TGDuration.QUARTER, 1);
		harness.update(SCORE | TAB);

		assertEquals("0.66005516..3.5000005 18.277779..30.722223", extent(first) + " " + extent(second));
	}

	@Test
	public void aRestIsMovedClearOfTheStemOfANoteInTheOtherVoice() {
		// voice 0 stems go up and push the rest down only; voice 1 stems go down and push it up only
		assertEquals(""
			+ "score: under an up stem 52.5..55.339947, over a down stem -20.339945..-17.5,"
			+ " clear of an up stem 11.160055..14.0, clear of a down stem 11.160055..14.0\n"
			+ "tablature: under an up stem 48.0..50.921085, over a down stem -10.921086..-8.0,"
			+ " clear of an up stem 21.078913..24.0, clear of a down stem 21.078913..24.0"
			, "score: " + restAgainstStems(SCORE | TAB, 6, 1) + "\ntablature: " + restAgainstStems(TAB, 6, 1));
	}

	// ---- helpers ----

	// the rest against a low note (lowString) and a high note (highString), with the note's stem up then down
	private static String restAgainstStems(int style, int lowString, int highString) {
		return "under an up stem " + restAgainstNote(style, lowString, 0, 1)
			+ ", over a down stem " + restAgainstNote(style, highString, 1, 0)
			+ ", clear of an up stem " + restAgainstNote(style, highString, 0, 1)
			+ ", clear of a down stem " + restAgainstNote(style, lowString, 1, 0);
	}

	// a quarter note (fret 5 on the given string) in one voice, a half rest in the other: returns the rest extent
	private static String restAgainstNote(int style, int string, int noteVoice, int restVoice) {
		LayoutHarness harness = new LayoutHarness();
		harness.note(0, TGDuration.QUARTER, noteVoice, string, 5);
		TGVoice rest = harness.rest(0, TGDuration.HALF, restVoice);
		harness.update(style);
		return extent(rest);
	}

	// "duration:minY..maxY" for a lone rest of each duration, at the start of an otherwise empty measure
	private static String restByDuration(int style) {
		StringBuilder text = new StringBuilder();
		for (int duration : DURATIONS) {
			LayoutHarness harness = new LayoutHarness();
			TGVoice rest = harness.rest(0, duration, 0);
			harness.update(style);
			text.append(text.length() == 0 ? "" : " ").append(duration).append(':').append(extent(rest));
		}
		return text.toString();
	}

	private static String extent(TGVoice voice) {
		TGVoiceImpl impl = (TGVoiceImpl) voice;
		return impl.getMinY() + ".." + impl.getMaxY();
	}
}
