package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;

/**
 * Pins the effects a note draws on the tablature after its fret (TGNoteImpl.paintEffects): a grace note's fret
 * before it; a V for a tremolo bar; a slide line to the next note on its string, rising or falling with it (raised
 * when level), or a fixed rising line when no note follows; a hammer arch to the next note or a fixed distance.
 *
 * Notes are quarter notes in a 4/4 measure printed at scale 2 and then widened by 40; a note's tablature is painted
 * alone at (100, 0), and only what follows its fret is kept.
 */
public class TestTablatureEffectPainting {

	private final LayoutHarness harness = new LayoutHarness();

	public TestTablatureEffectPainting() {
		harness.scale = 2f;
	}

	@Test
	public void aSlideRunsToTheNextNoteOnItsStringUpDownOrLevel() {
		TGNoteImpl up = slide(2, 5, 7);
		TGNoteImpl down = slide(3, 5, 3);
		TGNoteImpl level = slide(4, 5, 5);

		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 204.0 60.8\n"
			+ "lineTo 234.0 51.2\n"
			+ "closePath\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 204.0 67.2\n"
			+ "lineTo 234.0 76.8\n"
			+ "closePath\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 204.0 83.2\n"
			+ "lineTo 234.0 83.2\n"
			+ "closePath"
			, effects(up) + "\n--\n" + effects(down) + "\n--\n" + effects(level));
	}

	@Test
	public void aSlideWithoutANextNoteRunsAFixedDistance() {
		TGNoteImpl note = harness.note(3 * QUARTER, TGDuration.QUARTER, 0, 3, 5);
		note.getEffect().setSlide(true);

		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 354.0 67.2\n"
			+ "lineTo 380.0 67.2\n"
			+ "closePath"
			, effects(note));
	}

	@Test
	public void aHammerArchesToTheNextNoteOrAFixedDistance() {
		TGNoteImpl hammered = harness.note(0, TGDuration.QUARTER, 0, 3, 5);
		hammered.getEffect().setHammer(true);
		harness.note(2 * QUARTER, TGDuration.QUARTER, 0, 3, 7);
		TGNoteImpl last = harness.note(3 * QUARTER, TGDuration.QUARTER, 0, 3, 5);
		last.getEffect().setHammer(true);

		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath 2\n"
			+ "moveTo 204.0 72.0\n"
			+ "cubicTo 204.0 64.0 284.0 64.0 284.0 72.0\n"
			+ "cubicTo 284.0 68.0 204.0 68.0 204.0 72.0\n"
			+ "closePath\n"
			+ "--\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath 2\n"
			+ "moveTo 354.0 72.0\n"
			+ "cubicTo 354.0 64.0 374.0 64.0 374.0 72.0\n"
			+ "cubicTo 374.0 68.0 354.0 68.0 354.0 72.0\n"
			+ "closePath"
			, effects(hammered) + "\n--\n" + effects(last));
	}

	@Test
	public void aGraceNoteShowsItsFretBeforeTheNote() {
		TGNoteImpl note = harness.note(0, TGDuration.QUARTER, 0, 3, 5);
		TGEffectGrace grace = harness.factory.newEffectGrace();
		grace.setFret(3);
		note.getEffect().setGrace(grace);

		assertEquals(""
			+ "setFont font(0.0)\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "drawString '3' 188.0 76.0"
			, effects(note));
	}

	@Test
	public void aTremoloBarIsAVAfterTheNote() {
		TGNoteImpl note = harness.note(0, TGDuration.QUARTER, 0, 3, 5);
		TGEffectTremoloBar tremoloBar = harness.factory.newEffectTremoloBar();
		tremoloBar.addPoint(0, -2);
		note.getEffect().setTremoloBar(tremoloBar);

		assertEquals(""
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 2.0\n"
			+ "initPath\n"
			+ "moveTo 198.0 72.0\n"
			+ "lineTo 206.0 90.0\n"
			+ "moveTo 206.0 90.0\n"
			+ "lineTo 214.0 72.0\n"
			+ "closePath"
			, effects(note));
	}

	@Test
	public void aBendStartsAtItsNote() {
		TGNoteImpl note = harness.note(QUARTER, TGDuration.QUARTER, 0, 3, 5);
		harness.bend(note, 0, 4);

		effects(note);

		// the first point of the bend's arrow
		assertEquals(""
			+ "moveTo 246.88889 72.0"
			, harness.painter.callsStartingWith("moveTo").get(0));
	}

	// a note on the first beat sliding to a note at the given fret on the second, on the given string
	private TGNoteImpl slide(int string, int fret, int nextFret) {
		TGNoteImpl note = harness.note(0, TGDuration.QUARTER, 0, string, fret);
		note.getEffect().setSlide(true);
		harness.note(QUARTER, TGDuration.QUARTER, 0, string, nextFret);
		return note;
	}

	// prints the song, then paints the note's tablature alone: the calls after its fret
	private String effects(TGNoteImpl note) {
		harness.print(TAB);
		harness.measure().setSpacing(40f);
		harness.painter.clear();
		note.paintTablatureNote(harness.layout(), harness.painter, 100, 0, note.getBeatImpl().getSpacing(harness.layout()));
		List<String> calls = harness.painter.calls();
		int fret = 0;
		while (!calls.get(fret).startsWith("drawString '" + note.getValue() + "'")) {
			fret++;
		}
		return String.join("\n", calls.subList(fret + 1, calls.size()));
	}
}
