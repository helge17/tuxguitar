package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.CHORDS;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGStroke;

/**
 * Pins what a beat paints around its voices (TGBeatImpl.paint): offline, the chord, stroke and pick
 * stroke; in play mode, nothing of those, but a rounded box around the played beat when highlighted,
 * stretched to the notes outside the staff.
 *
 * The beat is the first of a 4/4 measure of quarter notes, laid out on a vertical screen layout whose
 * controller reports the beat as playing. Only the calls that follow the voices' painting are kept.
 */
public class TestBeatPainting {

	private final LayoutHarness harness = new LayoutHarness();
	private TGBeatImpl beat;

	public TestBeatPainting() {
		harness.quarterNotes(1, 2, 3);
		harness.note(0, TGDuration.QUARTER, 0, 3, 5);
		beat = harness.beatAt(0);
	}

	@Test
	public void offlineABeatPaintsItsStrokes() {
		beat.getStroke().setDirection(TGStroke.STROKE_UP);
		beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_DOWN);

		assertEquals(""
			+ "setLineWidth 0.0\n"
			+ "initPath\n"
			+ "moveTo 57.0 28.0\n"
			+ "lineTo 57.0 52.0\n"
			+ "moveTo 55.0 47.0\n"
			+ "lineTo 57.0 52.0\n"
			+ "moveTo 59.0 47.0\n"
			+ "lineTo 57.0 52.0\n"
			+ "closePath\n"
			+ "setLineWidth 0.0\n"
			+ "initPath\n"
			+ "moveTo 42.0 18.0\n"
			+ "lineTo 42.0 12.0\n"
			+ "moveTo 48.0 12.0\n"
			+ "lineTo 48.0 18.0\n"
			+ "closePath\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 42.0 12.0\n"
			+ "lineTo 48.0 12.0\n"
			+ "closePath"
			, afterVoices(TAB, false));
	}

	@Test
	public void offlineABeatPaintsItsChord() {
		TGChord chord = harness.factory.newChord(6);
		chord.setName("C");
		beat.setChord(chord);

		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "drawString 'C' 47.0 2.0"
			, afterVoices(TAB | CHORDS, false));
	}

	@Test
	public void inPlayModeStrokesAndChordsAreNotPainted() {
		beat.getStroke().setDirection(TGStroke.STROKE_UP);
		beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_DOWN);
		TGChord chord = harness.factory.newChord(6);
		chord.setName("C");
		beat.setChord(chord);
		harness.playMode = true;

		assertEquals("", afterVoices(TAB | CHORDS, false));
	}

	@Test
	public void thePlayedBeatIsBoxedOnTheTablatureAndOnTheScoreTheBoxStretchesToNotesOutsideTheStaff() {
		harness.playMode = true;
		String onTablature = afterVoices(TAB, true);
		notesOutsideTheStaff();

		assertEquals(""
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "setAntialias false\n"
			+ "moveTo 40.0 17.0\n"
			+ "cubicTo 38.343147 17.0 37.0 18.343145 37.0 20.0\n"
			+ "lineTo 37.0 70.0\n"
			+ "cubicTo 37.0 71.65685 38.343147 73.0 40.0 73.0\n"
			+ "lineTo 57.0 73.0\n"
			+ "cubicTo 58.656853 73.0 60.0 71.65685 60.0 70.0\n"
			+ "lineTo 60.0 20.0\n"
			+ "cubicTo 60.0 18.343145 58.656853 17.0 57.0 17.0\n"
			+ "lineTo 40.0 17.0\n"
			+ "closePath\n"
			+ "--\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "setAntialias false\n"
			+ "moveTo 76.0 13.0\n"
			+ "cubicTo 74.34315 13.0 73.0 14.343145 73.0 16.0\n"
			+ "lineTo 73.0 122.0\n"
			+ "cubicTo 73.0 123.65685 74.34315 125.0 76.0 125.0\n"
			+ "lineTo 93.0 125.0\n"
			+ "cubicTo 94.65685 125.0 96.0 123.65685 96.0 122.0\n"
			+ "lineTo 96.0 16.0\n"
			+ "cubicTo 96.0 14.343145 94.65685 13.0 93.0 13.0\n"
			+ "lineTo 76.0 13.0\n"
			+ "closePath"
			, onTablature + "\n--\n" + afterVoices(SCORE, true));
	}

	@Test
	public void theBoxStretchesOnlyUpwardsWhenTheTablatureIsShownToo() {
		notesOutsideTheStaff();
		harness.playMode = true;

		assertEquals(""
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "setAntialias false\n"
			+ "moveTo 76.0 13.0\n"
			+ "cubicTo 74.34315 13.0 73.0 14.343145 73.0 16.0\n"
			+ "lineTo 73.0 210.625\n"
			+ "cubicTo 73.0 212.28186 74.34315 213.625 76.0 213.625\n"
			+ "lineTo 93.0 213.625\n"
			+ "cubicTo 94.65685 213.625 96.0 212.28186 96.0 210.625\n"
			+ "lineTo 96.0 16.0\n"
			+ "cubicTo 96.0 14.343145 94.65685 13.0 93.0 13.0\n"
			+ "lineTo 76.0 13.0\n"
			+ "closePath"
			, afterVoices(SCORE | TAB, true));
	}

	@Test
	public void noBoxUnlessHighlightedPlayingAndInPlayMode() {
		String notHighlighted;
		String notPlaying;
		String offline;
		harness.playMode = true;
		notHighlighted = afterVoices(TAB, false);
		harness.playing = false;
		notPlaying = afterVoices(TAB, true);
		harness.playing = true;
		harness.playMode = false;
		offline = afterVoices(TAB, true);

		assertEquals("||", notHighlighted + "|" + notPlaying + "|" + offline);
	}

	// ---- helpers ----

	private void notesOutsideTheStaff() {
		harness.note(0, TGDuration.QUARTER, 0, 1, 20);
		harness.note(0, TGDuration.QUARTER, 0, 6, 0);
	}

	// what beat.paint draws after its voices: the voices are painted alone first, then the beat
	private String afterVoices(int style, boolean highlight) {
		TGLayout layout = harness.screen(style);
		int skipped = extraLineCalls(layout) + voiceCalls(layout);
		harness.painter.clear();
		beat.paint(layout, harness.painter, 0, 0, highlight);
		List<String> calls = harness.painter.calls();
		return String.join("\n", calls.subList(Math.min(skipped, calls.size()), calls.size()));
	}

	private int voiceCalls(TGLayout layout) {
		harness.painter.clear();
		for (int v = 0; v < TGBeat.MAX_VOICES; v++) {
			beat.getVoiceImpl(v).paint(layout, harness.painter, 0, 0);
		}
		return harness.painter.calls().size();
	}

	// offline with the score shown, the beat paints its ledger lines before its voices
	private int extraLineCalls(TGLayout layout) {
		if (layout.isPlayModeEnabled() || (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0) {
			return 0;
		}
		harness.painter.clear();
		beat.paintExtraLines(harness.painter, layout, 0, 0);
		int count = harness.painter.calls().size();
		harness.painter.clear();
		return count;
	}
}
