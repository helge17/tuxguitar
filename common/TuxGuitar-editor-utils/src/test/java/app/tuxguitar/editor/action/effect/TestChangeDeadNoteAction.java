package app.tuxguitar.editor.action.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGNoteRange;

/**
 * Pins TGChangeDeadNoteAction, the dead note toggle: selected notes all become dead, or all come back to life when
 * they all were dead. Without a selection and without a note under the caret, it adds a dead note of fret 0, at the
 * caret velocity, on the caret string and voice. A percussion track is left alone.
 *
 * Two beats are read back as "first | second", a dead note ending in "x".
 */
public class TestChangeDeadNoteAction {

	private static final int CARET_VELOCITY = 47;

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);

	public TestChangeDeadNoteAction() {
		fixture.caret(first, 3);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, CARET_VELOCITY);
	}

	private TGNote deadNote(TGBeat beat, int string, int fret) {
		TGNote note = fixture.note(beat, string, fret);
		note.getEffect().setDeadNote(true);
		return note;
	}

	private String changeDead() {
		fixture.execute(new TGChangeDeadNoteAction(fixture.context));
		return SongActionFixture.describe(first, TestChangeDeadNoteAction::dead) + " | " + SongActionFixture.describe(second, TestChangeDeadNoteAction::dead);
	}

	private static String dead(TGNote note) {
		return note.getEffect().isDeadNote() ? "x" : "";
	}

	@Test
	public void aSelectionWithALiveNoteBecomesAllDead() {
		fixture.select(fixture.note(first, 3, 5), deadNote(second, 2, 7));

		assertEquals("s3f5x | s2f7x", changeDead());
	}

	@Test
	public void aSelectionOfDeadNotesComesBackToLife() {
		fixture.select(deadNote(first, 3, 5), deadNote(second, 2, 7));

		assertEquals("s3f5 | s2f7", changeDead());
	}

	@Test
	public void aNoteThatTurnsDeadLosesItsTie() {
		TGNote tied = fixture.note(second, 3, 5);
		tied.setTiedNote(true);
		fixture.select(tied);

		changeDead();

		assertFalse(tied.isTiedNote());
	}

	@Test
	public void aPercussionSelectionIsLeftAlone() {
		fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
		fixture.select(fixture.note(first, 3, 5));

		assertEquals("s3f5 | ", changeDead());
	}

	@Test
	public void withoutSelectionADeadNoteIsAddedOnTheCaretString() {
		fixture.note(first, 2, 7);

		assertEquals("s2f7 s3f0x | ", changeDead());
	}

	@Test
	public void theAddedNoteTakesTheCaretVelocity() {
		changeDead();

		assertEquals("s3f0v" + CARET_VELOCITY, SongActionFixture.describe(first, note -> "v" + note.getVelocity()));
	}

	@Test
	public void anEmptySelectionAddsADeadNoteToo() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, TGNoteRange.empty());

		assertEquals("s3f0x | ", changeDead());
	}

	@Test
	public void theDeadNoteGoesInTheCaretVoice() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, first.getVoice(1));

		changeDead();

		assertEquals(" | 1", SongActionFixture.describe(first) + " | " + first.getVoice(1).countNotes());
	}

	@Test
	public void withoutSelectionTheNoteUnderTheCaretIsLeftAlone() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, fixture.note(first, 3, 5));

		assertEquals("s3f5 | ", changeDead());
	}

	@Test
	public void aPercussionTrackGetsNoDeadNoteWithoutSelection() {
		fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);

		assertEquals(" | ", changeDead());
	}
}
