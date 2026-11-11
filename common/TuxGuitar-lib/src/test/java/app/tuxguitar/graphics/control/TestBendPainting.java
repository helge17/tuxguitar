package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.COMPACT;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;

/**
 * Pins how a bent note is drawn on the tablature (TGNoteImpl.paintBend): the hold line, the
 * bend / release arrows and their amplitude labels, in normal and compact mode; and the extra
 * width a bend claims ("width=").
 *
 * The bent note is a quarter note, fret 5, at the start of a 4/4 measure followed by quarter notes.
 * Once the measure is printed, the note's tablature is painted alone at (0, 0), and read back as the
 * painter's calls that follow its fret number, one per line.
 */
public class TestBendPainting {

	private LayoutHarness harness = new LayoutHarness();
	private TGNoteImpl note;

	@Test
	public void aHoldIsTheSameDashedLineInBothModes() {
		bentNote(3, 4, 4);

		assertEquals(paint(TAB), paint(TAB | COMPACT));
		assertEquals(""
			+ "width=0.0\n"
			+ "initPath\n"
			+ "moveTo 41.0 8.0\n"
			+ "lineTo 43.5 8.0\n"
			+ "moveTo 46.0 8.0\n"
			+ "lineTo 48.5 8.0\n"
			+ "moveTo 51.0 8.0\n"
			+ "lineTo 53.5 8.0\n"
			+ "moveTo 56.0 8.0\n"
			+ "lineTo 58.5 8.0\n"
			+ "closePath"
			, paint(TAB));
	}

	@Test
	public void aFullBendIsACurvedArrowLabelledOne() {
		bentNote(3, 0, 4);

		assertEquals(""
			+ "width=10.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 50.0 36.0\n"
			+ "lineTo 51.0 36.0\n"
			+ "cubicTo 51.0 36.0 60.0 36.0 60.0 8.0\n"
			+ "moveTo 60.0 8.0\n"
			+ "lineTo 58.0 10.0\n"
			+ "moveTo 60.0 8.0\n"
			+ "lineTo 62.0 10.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 60.0 2.0"
			, paint(TAB));
	}

	@Test
	public void amplitudesAreLabelledInQuarterTonesShiftedLeftUnlessWholeTones() {
		bentNote(3, 0, 1);
		StringBuilder labels = new StringBuilder();
		for (int amplitude : new int[] {1, 2, 3, 4, 5, 8, 12, 13, 14}) {
			harness.bend(note, 0, amplitude);
			labels.append(amplitude).append(" -> ").append(labelsOf(note)).append('\n');
		}
		harness.scale = 2f;
		harness.bend(note, 0, 1);
		labels.append("1 at double scale -> ").append(labelsOf(note)).append('\n');

		assertEquals(""
			+ "1 -> drawString '1/4' 56.0 2.0\n"
			+ "2 -> drawString '1/2' 56.0 2.0\n"
			+ "3 -> drawString '3/4' 56.0 2.0\n"
			+ "4 -> drawString '1' 60.0 2.0\n"
			+ "5 -> drawString '1\u00BC' 56.0 2.0\n"
			+ "8 -> drawString '2' 60.0 2.0\n"
			+ "12 -> drawString '3' 60.0 2.0\n"
			+ "13 -> drawString '' 56.0 2.0\n"
			+ "14 -> drawString '' 56.0 2.0\n"
			+ "1 at double scale -> drawString '1/4' 110.0 2.0\n"
			, labels.toString());
	}

	@Test
	public void aReleaseStartsWithAPreBendArrow() {
		bentNote(3, 4, 0);

		assertEquals(""
			+ "width=10.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 50.0 8.0\n"
			+ "lineTo 50.0 36.0\n"
			+ "moveTo 50.0 8.0\n"
			+ "lineTo 48.0 10.0\n"
			+ "moveTo 50.0 8.0\n"
			+ "lineTo 52.0 10.0\n"
			+ "moveTo 50.0 8.0\n"
			+ "lineTo 51.0 8.0\n"
			+ "cubicTo 51.0 8.0 60.0 8.0 60.0 36.0\n"
			+ "moveTo 60.0 36.0\n"
			+ "lineTo 58.0 34.0\n"
			+ "moveTo 60.0 36.0\n"
			+ "lineTo 62.0 34.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 50.0 2.0"
			, paint(TAB));
	}

	@Test
	public void successiveMovementsAreChainedAndOnlyBendsAreLabelled() {
		bentNote(3, 0, 4, 0, 2);

		assertEquals(""
			+ "width=30.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 50.0 36.0\n"
			+ "lineTo 51.0 36.0\n"
			+ "cubicTo 51.0 36.0 60.0 36.0 60.0 8.0\n"
			+ "moveTo 60.0 8.0\n"
			+ "lineTo 58.0 10.0\n"
			+ "moveTo 60.0 8.0\n"
			+ "lineTo 62.0 10.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 60.0 2.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 60.0 8.0\n"
			+ "lineTo 61.0 8.0\n"
			+ "cubicTo 61.0 8.0 70.0 8.0 70.0 36.0\n"
			+ "moveTo 70.0 36.0\n"
			+ "lineTo 68.0 34.0\n"
			+ "moveTo 70.0 36.0\n"
			+ "lineTo 72.0 34.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 70.0 36.0\n"
			+ "lineTo 71.0 36.0\n"
			+ "cubicTo 71.0 36.0 80.0 36.0 80.0 8.0\n"
			+ "moveTo 80.0 8.0\n"
			+ "lineTo 78.0 10.0\n"
			+ "moveTo 80.0 8.0\n"
			+ "lineTo 82.0 10.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1/2' 76.0 2.0"
			, paint(TAB));
	}

	@Test
	public void ofSeveralBentNotesOnABeatOnlyTheHighestStringIsLabelledAndOnlyWhenAllBendAlike() {
		bentNote(3, 0, 4);
		TGNoteImpl higher = harness.note(0, TGDuration.QUARTER, 0, 2, 5);
		harness.bend(higher, 0, 4);
		String alike = labelsOf(note) + " | " + labelsOf(higher);
		harness.bend(higher, 0, 2);
		String different = labelsOf(note) + " | " + labelsOf(higher);

		assertEquals(""
			+ "alike:  | drawString '1' 60.0 2.0\n"
			+ "different:  | "
			, "alike: " + alike + "\ndifferent: " + different);
	}

	@Test
	public void compactModeDrawsTheFirstMovementOnlyAsAShortArrowTakingNoWidth() {
		// a release starts at the note, unless a higher string or the edge of the tablature is in the way
		String sections = compact("bend", 3, false, 0, 4)
			+ compact("release", 3, false, 4, 0)
			+ compact("release from the first string", 1, false, 4, 0)
			+ compact("release under a higher string", 3, true, 4, 0)
			+ compact("bend, release, bend", 3, false, 0, 4, 0, 2);

		assertEquals(""
			+ "-- bend\n"
			+ "width=0.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 50.0 36.0\n"
			+ "lineTo 51.0 36.0\n"
			+ "cubicTo 51.0 36.0 53.0 36.0 53.0 34.0\n"
			+ "moveTo 53.0 34.0\n"
			+ "lineTo 53.0 8.0\n"
			+ "moveTo 53.0 8.0\n"
			+ "lineTo 51.0 10.0\n"
			+ "moveTo 53.0 8.0\n"
			+ "lineTo 55.0 10.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 53.0 2.0\n"
			+ "-- release\n"
			+ "width=0.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 44.0 8.0\n"
			+ "lineTo 45.0 8.0\n"
			+ "cubicTo 45.0 8.0 47.0 8.0 47.0 10.0\n"
			+ "moveTo 47.0 10.0\n"
			+ "lineTo 47.0 30.0\n"
			+ "moveTo 47.0 30.0\n"
			+ "lineTo 45.0 28.0\n"
			+ "moveTo 47.0 30.0\n"
			+ "lineTo 49.0 28.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 44.0 2.0\n"
			+ "-- release from the first string\n"
			+ "width=0.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 50.0 8.0\n"
			+ "lineTo 51.0 8.0\n"
			+ "cubicTo 51.0 8.0 53.0 8.0 53.0 10.0\n"
			+ "moveTo 53.0 10.0\n"
			+ "lineTo 53.0 20.0\n"
			+ "moveTo 53.0 20.0\n"
			+ "lineTo 51.0 18.0\n"
			+ "moveTo 53.0 20.0\n"
			+ "lineTo 55.0 18.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 50.0 2.0\n"
			+ "-- release under a higher string\n"
			+ "width=0.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 50.0 8.0\n"
			+ "lineTo 51.0 8.0\n"
			+ "cubicTo 51.0 8.0 53.0 8.0 53.0 10.0\n"
			+ "moveTo 53.0 10.0\n"
			+ "lineTo 53.0 36.0\n"
			+ "moveTo 53.0 36.0\n"
			+ "lineTo 51.0 34.0\n"
			+ "moveTo 53.0 36.0\n"
			+ "lineTo 55.0 34.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 50.0 2.0\n"
			+ "-- bend, release, bend\n"
			+ "width=0.0\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setLineWidth 1.0\n"
			+ "initPath\n"
			+ "moveTo 50.0 36.0\n"
			+ "lineTo 51.0 36.0\n"
			+ "cubicTo 51.0 36.0 53.0 36.0 53.0 34.0\n"
			+ "moveTo 53.0 34.0\n"
			+ "lineTo 53.0 8.0\n"
			+ "moveTo 53.0 8.0\n"
			+ "lineTo 51.0 10.0\n"
			+ "moveTo 53.0 8.0\n"
			+ "lineTo 55.0 10.0\n"
			+ "closePath\n"
			+ "setForeground rgb(0,0,0)\n"
			+ "setBackground rgb(0,0,0)\n"
			+ "setFont font(0.0)\n"
			+ "drawString '1' 53.0 2.0\n"
			, sections);
	}

	// ---- helpers ----

	// the bent note at the start of the measure, fret 5 on the given string, followed by quarter notes
	private void bentNote(int string, int... values) {
		harness.quarterNotes(1, 2, 3);
		note = harness.note(0, TGDuration.QUARTER, 0, string, 5);
		harness.bend(note, values);
	}

	// "-- title", then the painting in compact mode of a fresh bent note, alone or under a note on a higher string
	private String compact(String title, int string, boolean higherNote, int... values) {
		harness = new LayoutHarness();
		bentNote(string, values);
		if (higherNote) {
			harness.note(0, TGDuration.QUARTER, 0, string - 1, 5);
		}
		return "-- " + title + "\n" + paint(TAB | COMPACT) + "\n";
	}

	private String paint(int style) {
		List<String> calls = paintAlone(style, note);
		return "width=" + note.getEffectWidth(harness.layout()) + "\n" + String.join("\n", afterFretNumber(calls));
	}

	// only the amplitude labels: the strings painted after the fret number
	private String labelsOf(TGNoteImpl painted) {
		paintAlone(TAB, painted);
		List<String> strings = harness.painter.callsStartingWith("drawString '");
		return String.join(", ", strings.subList(1, strings.size()));
	}

	// prints the measure, then paints the tablature of the given note alone
	private List<String> paintAlone(int style, TGNoteImpl painted) {
		harness.print(style);
		harness.painter.clear();
		painted.paintTablatureNote(harness.layout(), harness.painter, 0, 0, 0);
		return harness.painter.calls();
	}

	private static List<String> afterFretNumber(List<String> calls) {
		for (int i = 0; i < calls.size(); i++) {
			if (calls.get(i).startsWith("drawString '5'")) {
				return calls.subList(i + 1, calls.size());
			}
		}
		return calls;
	}
}
