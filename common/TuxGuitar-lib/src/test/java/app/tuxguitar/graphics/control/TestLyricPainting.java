package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.ui.resource.UIColorModel;

/**
 * Pins how a track's lyrics are painted under its measures (TGLyricImpl.paintCurrentNoteBeats): one word per beat
 * with notes, rests skipped, from the measure the lyrics start at; an empty word leaves its beat blank; words run
 * out before the notes do. In play mode, the words of a played beat are in the play color.
 *
 * The song has two 4/4 measures of quarter notes, the second beat of the first one a rest. Words are listed as
 * "'word' x y in color" in the order painted. Lyrics are blue, the played note red.
 */
public class TestLyricPainting {

	private final LayoutHarness harness = new LayoutHarness();

	public TestLyricPainting() {
		harness.songManager.addNewMeasureBeforeEnd(harness.song);
		harness.quarterNotes(0, 2, 3);
		harness.rest(QUARTER, TGDuration.QUARTER, 0);
		for (int quarter = 0; quarter < 4; quarter++) {
			harness.note(1, quarter * QUARTER, TGDuration.QUARTER, 0, 3, 5);
		}
		harness.styles.setForegroundColor(new UIColorModel(0, 0, 255));
		harness.styles.setPlayNoteColor(new UIColorModel(255, 0, 0));
	}

	@Test
	public void eachBeatWithNotesGetsTheNextWord() {
		assertEquals(""
			+ "'a' 80.0 146.0 in rgb(0,0,255)\n"
			+ "'b' 120.0 146.0 in rgb(0,0,255)\n"
			+ "'c' 140.0 146.0 in rgb(0,0,255)\n"
			+ "'d' 200.0 146.0 in rgb(0,0,255)\n"
			+ "'e' 220.0 146.0 in rgb(0,0,255)\n"
			+ "'f' 240.0 146.0 in rgb(0,0,255)\n"
			+ "'g' 260.0 146.0 in rgb(0,0,255)\n"
			, words("a b c d e f g h"));
	}

	@Test
	public void anEmptyWordLeavesItsBeatBlankAndTheWordsRunOut() {
		assertEquals(""
			+ "'a' 80.0 146.0 in rgb(0,0,255)\n"
			+ "'c' 140.0 146.0 in rgb(0,0,255)\n"
			+ "'d' 200.0 146.0 in rgb(0,0,255)\n"
			, words("a  c d"));
	}

	@Test
	public void lyricsStartAtTheirMeasure() {
		harness.song.getTrack(0).getLyrics().setFrom(2);

		assertEquals(""
			+ "'a' 200.0 146.0 in rgb(0,0,255)\n"
			+ "'b' 220.0 146.0 in rgb(0,0,255)\n"
			+ "'c' 240.0 146.0 in rgb(0,0,255)\n"
			, words("a b c"));
	}

	@Test
	public void inPlayModeThePlayedWordsAreInThePlayColor() {
		harness.song.getTrack(0).getLyrics().setLyrics("a b c");
		harness.playMode = true;
		TGLayout layout = harness.screen(TAB);
		harness.painter.clear();
		layout.paintPlayMode(harness.painter, harness.measure(), harness.beatAt(0));

		assertEquals(""
			+ "'a' 75.0 86.0 in rgb(255,0,0)\n"
			+ "'b' 115.0 86.0 in rgb(255,0,0)\n"
			+ "'c' 135.0 86.0 in rgb(255,0,0)\n"
			, lyricCalls());
	}

	@Test
	public void inPlayModeTheWordsOfABeatNotPlayedKeepTheirColor() {
		harness.song.getTrack(0).getLyrics().setLyrics("a");
		harness.playMode = true;
		TGLayout layout = harness.screen(TAB);
		harness.playing = false;
		harness.painter.clear();
		layout.paintPlayMode(harness.painter, harness.measure(), harness.beatAt(0));

		assertEquals("'a' 75.0 86.0 in rgb(0,0,255)\n", lyricCalls());
	}

	@Test
	public void wordsFollowTheirBeatsAcrossAWiderMeasure() {
		harness.song.getTrack(0).getLyrics().setLyrics("a b c");
		harness.print(TAB);
		harness.measure().setSpacing(40f);
		harness.painter.clear();
		((TGLyricImpl) harness.song.getTrack(0).getLyrics()).paintCurrentNoteBeats(harness.painter, harness.layout(), harness.measure(), 100, 0);

		assertEquals(""
			+ "'a' 160.0 81.0 in rgb(0,0,255)\n"
			+ "'b' 220.0 81.0 in rgb(0,0,255)\n"
			+ "'c' 250.0 81.0 in rgb(0,0,255)\n"
			+ ""
			, lyricCalls());
	}

	private String words(String lyrics) {
		harness.song.getTrack(0).getLyrics().setLyrics(lyrics);
		harness.print(TAB);
		return lyricCalls();
	}

	// the words drawn (lowercase or empty strings: no title, tempo or measure number), with the color they were drawn in
	private String lyricCalls() {
		StringBuilder words = new StringBuilder();
		String color = "";
		for (String call : harness.painter.calls()) {
			if (call.startsWith("setForeground ")) {
				color = call.substring("setForeground ".length());
			} else if (call.matches("drawString '[a-z]*' .*")) {
				words.append(call.substring("drawString ".length())).append(" in ").append(color).append('\n');
			}
		}
		return words.toString();
	}
}
