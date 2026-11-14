package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGChangeNoteAction, putting a note of a given fret and velocity on the caret string, unless the fret passes
 * the track's last fret (a percussion track has none), or the track is an audio track, which holds no notes. Normally
 * the note goes in the beat at the given position, and
 * only with a fret of 0 or more. In free edition mode it goes in the caret beat, whatever the fret, taking the given
 * duration; without a caret beat it goes at the position as normally.
 *
 * The measure holds four quarter beats, read back as "first | second | third | fourth" with each note's velocity,
 * then "success" when the action reported it.
 */
public class TestChangeNoteAction {

	private static final int VELOCITY = 47;

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGTrack track = fixture.song.getTrack(0);
	private final TGMeasure measure = track.getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);
	private final TGBeat third = fixture.beat(measure, 2);
	private final TGBeat fourth = fixture.beat(measure, 3);

	public TestChangeNoteAction() {
		fixture.caret(first, 3);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, third.getStart());
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, VELOCITY);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, duration(TGDuration.QUARTER));
	}

	private TGDuration duration(int value) {
		TGDuration duration = fixture.songManager.getFactory().newDuration();
		duration.setValue(value);
		return duration;
	}

	private String changeNote(int fret) {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, fret);
		fixture.execute(new TGChangeNoteAction(fixture.context));
		String outcome = fixture.outcome();
		for (TGBeat beat : new TGBeat[] { fourth, third, second, first }) {
			outcome = SongActionFixture.describe(beat, note -> "v" + note.getVelocity()) + " | " + outcome;
		}
		return outcome;
	}

	private void freeEditionMode() {
		fixture.songManager.setFreeEditionMode(true);
	}

	// with the caret in the second voice, how many notes the beat holds there after the change
	private int notesInTheSecondVoiceOf(TGBeat beat) {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, first.getVoice(1));
		changeNote(5);
		return beat.getVoice(1).countNotes();
	}

	private void freeEditionModeWithoutCaretBeat() {
		freeEditionMode();
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, null);
	}

	@Test
	public void theNoteGoesAtThePosition() {
		assertEquals(" |  | s3f5v47 |  | success", changeNote(5));
	}

	@Test
	public void theNoteReplacesTheOneOnTheSameString() {
		fixture.note(third, 3, 7);
		fixture.note(third, 2, 8);

		assertEquals(" |  | s2f8v95 s3f5v47 |  | success", changeNote(5));
	}

	@Test
	public void theFretsFromTheOpenStringToTheLastAreAccepted() {
		assertEquals(" |  | s3f0v47 |  | success", changeNote(0));
		assertEquals(" |  | s3f29v47 |  | success", changeNote(track.getMaxFret()));
	}

	@Test
	public void aFretPastTheLastIsRefused() {
		assertEquals(" |  |  |  | ", changeNote(track.getMaxFret() + 1));
	}

	@Test
	public void aPercussionTrackHasNoLastFret() {
		fixture.song.getChannel(0).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);

		assertEquals(" |  | s3f35v47 |  | success", changeNote(35));
	}

	@Test
	public void anAudioTrackHoldsNoNotes() {
		fixture.song.getChannel(0).setAudioInput(TGAudioInput.mono(1));

		assertEquals(" |  |  |  | ", changeNote(5));
	}

	@Test
	public void aNegativeFretIsRefused() {
		assertEquals(" |  |  |  | ", changeNote(-1));
	}

	@Test
	public void withoutATrackNothingIsAdded() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, null);

		assertEquals(" |  |  |  | ", changeNote(5));
	}

	@Test
	public void theNoteGoesInTheCaretVoice() {
		assertEquals(1, notesInTheSecondVoiceOf(third));
	}

	@Test
	public void inFreeEditionModeTheNoteGoesInTheCaretBeat() {
		freeEditionMode();

		assertEquals("s3f5v47 |  |  |  | success", changeNote(5));
	}

	@Test
	public void anIncompleteMeasureIsEditedFreely() {
		fixture.songManager.getMeasureManager().removeBeat(fourth);

		assertEquals("s3f5v47 |  |  |  | success", changeNote(5));
	}

	@Test
	public void inFreeEditionModeTheCaretBeatTakesTheDuration() {
		freeEditionMode();
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, duration(TGDuration.HALF));

		changeNote(5);

		assertEquals(TGDuration.HALF, first.getVoice(0).getDuration().getValue());
	}

	@Test
	public void inFreeEditionModeANegativeFretGoesInTheCaretBeat() {
		freeEditionMode();

		assertEquals("s3f-1v47 |  |  |  | success", changeNote(-1));
	}

	@Test
	public void inFreeEditionModeTheNoteGoesInTheCaretVoice() {
		freeEditionMode();

		assertEquals(1, notesInTheSecondVoiceOf(first));
	}

	@Test
	public void inFreeEditionModeWithoutACaretBeatTheNoteGoesAtThePosition() {
		freeEditionModeWithoutCaretBeat();

		assertEquals(" |  | s3f5v47 |  | success", changeNote(5));
	}

	@Test
	public void inFreeEditionModeWithoutCaretBeatNorMeasureNothingIsAdded() {
		freeEditionModeWithoutCaretBeat();
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, null);

		assertEquals(" |  |  |  | ", changeNote(5));
	}
}
