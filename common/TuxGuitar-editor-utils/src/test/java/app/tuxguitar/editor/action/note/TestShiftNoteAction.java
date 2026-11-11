package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;

/**
 * Pins TGShiftNoteAction (through TGShiftNoteUpAction and TGShiftNoteDownAction): each selected note moves to the next
 * string up (lower number) or down at the same pitch, the notes nearest that way first so they make room for the
 * others; the caret string follows the caret note; success is reported only when the selection holds a single beat.
 *
 * The track is a guitar in standard tuning (E4 B3 G3 D3 A2 E2); two beats are read back as "first | second".
 */
public class TestShiftNoteAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);

	private String run(TGShiftNoteAction action) {
		fixture.execute(action);
		return fixture.outcome(first, second);
	}

	private String up(TGNote... notes) {
		fixture.select(notes);
		return run(new TGShiftNoteUpAction(fixture.context));
	}

	private String down(TGNote... notes) {
		fixture.select(notes);
		return run(new TGShiftNoteDownAction(fixture.context));
	}

	private TGString caretString() {
		return (TGString) fixture.actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
	}

	@Test
	public void shiftingUpMovesTheNoteToTheHigherStringAtTheSamePitch() {
		assertEquals("s2f1 |  | success", up(fixture.note(first, 3, 5)));
	}

	@Test
	public void shiftingDownMovesTheNoteToTheLowerStringAtTheSamePitch() {
		assertEquals("s4f10 |  | success", down(fixture.note(first, 3, 5)));
	}

	@Test
	public void shiftingUpMovesTheHighestNoteFirst() {
		TGNote low = fixture.note(first, 3, 5);
		TGNote high = fixture.note(first, 2, 5);

		assertEquals("s1f0 s2f1 |  | success", up(low, high));
	}

	@Test
	public void shiftingDownMovesTheLowestNoteFirst() {
		TGNote high = fixture.note(first, 2, 5);
		TGNote low = fixture.note(first, 3, 5);

		assertEquals("s3f9 s4f10 |  | success", down(high, low));
	}

	@Test
	public void aNoteWithoutRoomStays() {
		assertEquals("s1f5 |  | success", up(fixture.note(first, 1, 5)));
	}

	@Test
	public void notesOfSeveralBeatsShiftWithoutReportingSuccess() {
		assertEquals("s2f1 | s4f0", up(fixture.note(first, 3, 5), fixture.note(second, 5, 5)));
	}

	@Test
	public void theCaretStringFollowsTheCaretNote() {
		TGNote caret = fixture.note(first, 3, 5);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, caret);

		up(fixture.note(first, 6, 5), caret);

		assertEquals(2, caretString().getNumber());
	}

	@Test
	public void theCaretStringStaysWhenTheCaretNoteCannotShift() {
		TGNote caret = fixture.note(first, 1, 5);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, caret);

		assertEquals("s1f5 s2f1 |  | success", up(caret, fixture.note(first, 3, 5)));
		assertNull(caretString());
	}

	@Test
	public void theCaretStringStaysWhenTheCaretNoteIsNotSelected() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, fixture.note(second, 3, 5));

		assertEquals("s2f1 | s3f5 | success", up(fixture.note(first, 3, 5)));
		assertNull(caretString());
	}

	@Test
	public void withoutSelectedNotesNothingShifts() {
		TGNote note = fixture.note(first, 3, 5);

		assertEquals("s3f5 | ", run(new TGShiftNoteUpAction(fixture.context)));
		assertEquals("s3f5 | ", up());
		assertEquals("s2f1 |  | success", up(note));
	}
}
