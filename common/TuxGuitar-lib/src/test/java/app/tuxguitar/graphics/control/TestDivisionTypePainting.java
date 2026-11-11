package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.QUARTER;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;

/**
 * Pins how a measure brackets its tuplets (TGMeasureImpl.paintDivisionTypes): where a bracket starts and
 * ends, and its label. A tuplet ends when the division changes, when its length reaches a multiple of
 * (shortest note x enters), or at the end of the measure.
 *
 * Once the 4/4 measure is printed, its brackets are painted alone at (0, 0); only the bracket ends
 * ("moveTo x y") and the labels ("drawString") are kept, one per line.
 */
public class TestDivisionTypePainting {

	private static final TGDivisionType TRIPLET = TGDivisionType.DIVISION_TYPES[1];
	private static final TGDivisionType QUINTUPLET = TGDivisionType.DIVISION_TYPES[2];

	private final LayoutHarness harness = new LayoutHarness();

	@Test
	public void aMeasureWithoutTupletsHasNoBracket() {
		harness.quarterNotes(0, 1, 2, 3);

		assertEquals("", brackets());
	}

	@Test
	public void aTripletFollowedByAnotherDivisionIsBracketedAlone() {
		tuplet(0, 0, TGDuration.EIGHTH, TRIPLET, 3);
		harness.quarterNotes(1, 2, 3);

		assertEquals(""
			+ "moveTo 40.0 10.0\n"
			+ "moveTo 91.0 10.0\n"
			+ "drawString '3' 63.5 10.0"
			, brackets());
	}

	@Test
	public void consecutiveTripletsGetOneBracketEach() {
		tuplet(0, 0, TGDuration.EIGHTH, TRIPLET, 6);
		harness.quarterNotes(2, 3);

		assertEquals(""
			+ "moveTo 40.0 10.0\n"
			+ "moveTo 91.0 10.0\n"
			+ "drawString '3' 63.5 10.0\n"
			+ "moveTo 94.0 10.0\n"
			+ "moveTo 145.0 10.0\n"
			+ "drawString '3' 117.5 10.0"
			, brackets());
	}

	@Test
	public void aChangeOfTupletDivisionStartsANewBracket() {
		tuplet(0, 0, TGDuration.EIGHTH, TRIPLET, 3);
		tuplet(QUARTER, 0, TGDuration.SIXTEENTH, QUINTUPLET, 5);
		harness.quarterNotes(2, 3);

		assertEquals(""
			+ "moveTo 40.0 10.0\n"
			+ "moveTo 108.333336 10.0\n"
			+ "drawString '3' 72.16667 10.0\n"
			+ "moveTo 120.0 10.0\n"
			+ "moveTo 199.0 10.0\n"
			+ "drawString '5' 157.5 10.0"
			, brackets());
	}

	@Test
	public void aTupletEndingTheMeasureIsBracketedAtTheEnd() {
		harness.quarterNotes(0, 1, 2);
		tuplet(3 * QUARTER, 0, TGDuration.EIGHTH, TRIPLET, 3);

		assertEquals(""
			+ "moveTo 202.0 10.0\n"
			+ "moveTo 253.0 10.0\n"
			+ "drawString '3' 225.5 10.0"
			, brackets());
	}

	@Test
	public void mixedValuesCloseOnceTheLengthIsAMultipleOfTheShortestTimesEnters() {
		long end = tuplet(0, 0, TGDuration.QUARTER, TRIPLET, 1);
		end = tuplet(end, 0, TGDuration.EIGHTH, TRIPLET, 1);
		tuplet(end, 0, TGDuration.EIGHTH, TRIPLET, 3);
		harness.quarterNotes(2, 3);

		assertEquals(""
			+ "moveTo 40.0 10.0\n"
			+ "moveTo 91.0 10.0\n"
			+ "drawString '3' 63.5 10.0\n"
			+ "moveTo 94.0 10.0\n"
			+ "moveTo 145.0 10.0\n"
			+ "drawString '3' 117.5 10.0"
			, brackets());
	}

	@Test
	public void theSecondVoiceIsBracketedInItsOwnRow() {
		harness.quarterNotes(0, 1, 2, 3);
		tuplet(0, 1, TGDuration.EIGHTH, TRIPLET, 3);

		assertEquals(""
			+ "moveTo 40.0 82.0\n"
			+ "moveTo 91.0 82.0\n"
			+ "drawString '3' 63.5 90.0"
			, brackets());
	}

	@Test
	public void aBeatEmptyInTheVoiceDoesNotBreakTheTuplet() {
		tuplet(0, 0, TGDuration.EIGHTH, TRIPLET, 3);
		harness.quarterNotes(1, 2, 3);
		harness.note(QUARTER / 2, TGDuration.EIGHTH, 1, 1, 0);
		harness.note(QUARTER, TGDuration.QUARTER, 1, 1, 0);

		assertEquals(""
			+ "moveTo 40.0 8.0\n"
			+ "moveTo 91.0 8.0\n"
			+ "drawString '3' 63.5 8.0"
			, brackets());
	}

	// ---- helpers ----

	// count notes of the given value and division in a row from start; returns where the last one ends
	private long tuplet(long start, int voiceIndex, int value, TGDivisionType division, int count) {
		long offset = start;
		for (int i = 0; i < count; i++) {
			TGNoteImpl note = harness.note(offset, value, voiceIndex, 3 + voiceIndex, 5);
			TGDuration duration = note.getVoice().getDuration();
			duration.getDivision().copyFrom(division);
			offset += duration.getPreciseTime();
		}
		return offset;
	}

	private String brackets() {
		harness.print(TAB);
		harness.painter.clear();
		harness.measure().paintDivisionTypes(harness.layout(), harness.painter, 0, 0);
		List<String> kept = harness.painter.calls().stream()
			.filter(call -> call.startsWith("moveTo") || call.startsWith("drawString"))
			.collect(Collectors.toList());
		return String.join("\n", kept);
	}
}
