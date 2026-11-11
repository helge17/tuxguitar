package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.ui.resource.UIColorModel;

/**
 * Pins how a note paints itself on the tablature (TGNoteImpl.paintTablatureNote): its fret, or, when tied and the
 * score is hidden, a tie from the note it is tied to (the last earlier note on its string, unless a rest comes
 * first) or from a fixed distance; then, offline, its effects, with tremolo picking strokes under the tablature
 * when the score is hidden. Nothing without the tablature; the played note is colored as playing.
 *
 * The measure is 4/4: quarter notes on string 3 at the quarters given by each test. Once the song is laid out, the
 * note is painted alone at (0, 0), one call per line. The play note color is red.
 */
public class TestTablatureNotePainting {

	private static final String BLACK = "rgb(0,0,0)";
	private static final String RED = "rgb(255,0,0)";

	private final LayoutHarness harness = new LayoutHarness();

	public TestTablatureNotePainting() {
		harness.styles.setPlayNoteColor(new UIColorModel(255, 0, 0));
	}

	@Test
	public void withoutTheTablatureNothingIsPainted() {
		TGNoteImpl note = note(0);

		assertEquals("", paint(note, SCORE));
	}

	@Test
	public void aNoteShowsItsFret() {
		TGNoteImpl note = note(0);

		assertEquals(fret(BLACK), paint(note, TAB));
	}

	@Test
	public void aTiedNoteIsTiedFromTheLastEarlierNoteOnItsString() {
		note(0);
		harness.note(1 * QUARTER, TGDuration.QUARTER, 0, 2, 7);
		TGNoteImpl tied = tied(2);
		note(3);

		assertEquals(tie(BLACK, 52, 87), paint(tied, TAB));
	}

	@Test
	public void withoutAnEarlierNoteOnItsStringOrAfterARestATieStartsAtAFixedDistance() {
		harness.note(0, TGDuration.QUARTER, 0, 2, 7);
		TGNoteImpl first = tied(1);
		harness.rest(2 * QUARTER, TGDuration.QUARTER, 0);
		TGNoteImpl afterRest = tied(3);

		assertEquals(tie(BLACK, 51, 67) + "\n--\n" + tie(BLACK, 91, 107), paint(first, TAB) + "\n--\n" + paint(afterRest, TAB));
	}

	@Test
	public void withTheScoreATiedNoteLeavesTheTablatureEmpty() {
		note(0);
		TGNoteImpl tied = tied(1);

		assertEquals("", paint(tied, SCORE | TAB));
	}

	@Test
	public void tremoloPickingStrokesOneMorePerShorterDuration() {
		TGNoteImpl note = note(0);

		assertEquals(fret(BLACK) + "\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 44.0 71.0\n"
			+ "lineTo 51.0 73.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0\n"
			+ "--\n"
			+ fret(BLACK) + "\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 44.0 71.0\n"
			+ "lineTo 51.0 73.0\n"
			+ "moveTo 44.0 75.0\n"
			+ "lineTo 51.0 77.0\n"
			+ "moveTo 44.0 79.0\n"
			+ "lineTo 51.0 81.0\n"
			+ "closePath\n"
			+ "setLineWidth 1.0"
			, tremolo(note, TAB, TGDuration.EIGHTH) + "\n--\n" + tremolo(note, TAB, TGDuration.THIRTY_SECOND));
	}

	@Test
	public void withTheScoreTremoloPickingIsNotStrokedOnTheTablature() {
		TGNoteImpl note = note(0);

		assertEquals(paint(note, SCORE | TAB), tremolo(note, SCORE | TAB, TGDuration.EIGHTH));
	}

	@Test
	public void inPlayModeEffectsAreHiddenAndThePlayedNoteIsColored() {
		TGNoteImpl note = note(0);
		note.getEffect().setTremoloPicking(tremoloPicking(TGDuration.EIGHTH));
		harness.playMode = true;
		String played = screen(note);
		harness.playing = false;

		assertEquals(fret(RED) + "\n--\n" + fret(BLACK), played + "\n--\n" + screen(note));
	}

	@Test
	public void inPlayModeATiedNoteIsColoredWhenPlayed() {
		note(0);
		TGNoteImpl tied = tied(1);
		harness.playMode = true;

		assertEquals(tie(RED, 52, 67), screen(tied));
	}

	// ---- helpers ----

	// fret 5 on its background, centered on string 3 at x 47, in the given color
	private static String fret(String color) {
		return ""
			+ "setForeground " + color + "\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "initPath 2\n"
			+ "addRectangle 45.0 38.0 4.0 -4.0\n"
			+ "closePath\n"
			+ "setForeground " + color + "\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '5' 45.0 40.0";
	}

	// a filled tie just under string 3, from x1 to x2, in the given color
	private static String tie(String color, int x1, int x2) {
		return ""
			+ "setForeground " + color + "\n"
			+ "setBackground " + color + "\n"
			+ "initPath 2\n"
			+ "moveTo " + x1 + ".0 40.0\n"
			+ "cubicTo " + x1 + ".0 42.666668 " + x2 + ".0 42.666668 " + x2 + ".0 40.0\n"
			+ "cubicTo " + x2 + ".0 44.666668 " + x1 + ".0 44.666668 " + x1 + ".0 40.0\n"
			+ "closePath";
	}

	private TGNoteImpl note(int quarter) {
		return harness.note(quarter * QUARTER, TGDuration.QUARTER, 0, 3, 5);
	}

	private TGNoteImpl tied(int quarter) {
		TGNoteImpl note = note(quarter);
		note.setTiedNote(true);
		return note;
	}

	private TGEffectTremoloPicking tremoloPicking(int durationValue) {
		TGEffectTremoloPicking tremoloPicking = harness.factory.newEffectTremoloPicking();
		tremoloPicking.getDuration().setValue(durationValue);
		return tremoloPicking;
	}

	private String tremolo(TGNoteImpl note, int style, int durationValue) {
		note.getEffect().setTremoloPicking(tremoloPicking(durationValue));
		return paint(note, style);
	}

	// prints the song, then paints the note's tablature alone
	private String paint(TGNoteImpl note, int style) {
		harness.print(style);
		return paintAlone(note, harness.layout());
	}

	// paints the song on a screen, then the note's tablature alone
	private String screen(TGNoteImpl note) {
		return paintAlone(note, harness.screen(TAB));
	}

	private String paintAlone(TGNoteImpl note, TGLayout layout) {
		harness.painter.clear();
		note.paintTablatureNote(layout, harness.painter, 0, 0, note.getBeatImpl().getSpacing(layout));
		return String.join("\n", harness.painter.calls());
	}
}
