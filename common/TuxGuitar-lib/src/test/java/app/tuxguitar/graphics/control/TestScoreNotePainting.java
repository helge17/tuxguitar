package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;

/**
 * Pins how a note paints itself on the score (TGNoteImpl.paint, score part): the accidental it needs in its key
 * before the head (sharp, flat or natural; none for a dead note), and, for a whole note, staccato under and
 * tremolo picking strokes over it.
 *
 * Notes are on the high E string in a 4/4 measure; once printed, the note is painted alone at (0, 0). An
 * accidental is described by its first point and its number of drawing calls (the head at y 17..23 follows it).
 */
public class TestScoreNotePainting {

	private final LayoutHarness harness = new LayoutHarness();

	@Test
	public void aSharpNoteHasASharp() {
		assertEquals("moveTo 78.78134 14.427292 x69", accidental(harness.note(0, TGDuration.QUARTER, 0, 1, 2)));
	}

	@Test
	public void anEnharmonicSharpIsWrittenAsAFlat() {
		TGNoteImpl note = harness.note(0, TGDuration.QUARTER, 0, 1, 2);
		note.toggleAltEnharmonic();

		assertEquals("moveTo 76.00257 9.904694 x30", accidental(note));
	}

	@Test
	public void aNoteBackToNaturalAfterASharpHasANatural() {
		harness.note(0, TGDuration.QUARTER, 0, 1, 2);

		assertEquals("moveTo 96.02216 13.0150795 x30", accidental(harness.note(QUARTER, TGDuration.QUARTER, 0, 1, 1)));
	}

	@Test
	public void aDeadNoteHasNoAccidental() {
		TGNoteImpl note = harness.note(0, TGDuration.QUARTER, 0, 1, 2);
		note.getEffect().setDeadNote(true);

		assertEquals("none", accidental(note));
	}

	@Test
	public void aWholeNoteHasStaccatoUnderAndTremoloPickingOverItScaledWithTheLayout() {
		TGNoteImpl note = harness.note(0, TGDuration.WHOLE, 0, 1, 3);
		note.getEffect().setStaccato(true);
		harness.scale = 2f;
		TGEffectTremoloPicking tremoloPicking = harness.factory.newEffectTremoloPicking();
		tremoloPicking.getDuration().setValue(TGDuration.EIGHTH);
		note.getEffect().setTremoloPicking(tremoloPicking);

		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath 2\n"
			+ "moveTo 169.595 48.0\n"
			+ "addCircle 169.595 48.0 6.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 4.0\n"
			+ "initPath\n"
			+ "moveTo 163.595 10.0\n"
			+ "lineTo 177.595 6.0\n"
			+ "closePath\n"
			+ "setLineWidth 2.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)"
			, afterHead(note));
	}

	private String paint(TGNoteImpl note) {
		harness.print(SCORE);
		harness.painter.clear();
		note.paint(harness.layout(), harness.painter, 0, 0);
		return String.join("\n", harness.painter.calls());
	}

	// what the note draws after its head (the head ends with the first closePath)
	private String afterHead(TGNoteImpl note) {
		String calls = paint(note);
		return calls.substring(calls.indexOf("closePath") + "closePath".length() + 1);
	}

	// the accidental path drawn before the note head: its first point and its number of drawing calls
	private String accidental(TGNoteImpl note) {
		List<String> calls = Arrays.asList(paint(note).split("\n"));
		if (!calls.get(3).equals("setLineWidth 0.0")) {
			return "none";
		}
		return calls.get(4) + " x" + (calls.indexOf("closePath") - 4);
	}
}
