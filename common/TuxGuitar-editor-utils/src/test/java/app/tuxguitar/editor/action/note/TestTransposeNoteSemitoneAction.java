package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;

/**
 * Pins TGTransposeNoteSemitoneAction (through TGIncrementNoteSemitoneAction and TGDecrementNoteSemitoneAction): every
 * selected note moves one fret up or down, or none does when one of them would leave the frets (0 to the track's last,
 * 29); success is reported only when the selection holds a single beat.
 *
 * Two beats are read back as "first | second".
 */
public class TestTransposeNoteSemitoneAction {

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGMeasure measure = fixture.song.getTrack(0).getMeasure(0);
	private final TGBeat first = fixture.beat(measure, 0);
	private final TGBeat second = fixture.beat(measure, 1);

	private String run(TGTransposeNoteSemitoneAction action) {
		fixture.execute(action);
		return fixture.outcome(first, second);
	}

	private String increment(TGNote... notes) {
		fixture.select(notes);
		return run(new TGIncrementNoteSemitoneAction(fixture.context));
	}

	private String decrement(TGNote... notes) {
		fixture.select(notes);
		return run(new TGDecrementNoteSemitoneAction(fixture.context));
	}

	@Test
	public void incrementingRaisesEverySelectedNote() {
		assertEquals("s2f6 s3f1 |  | success", increment(fixture.note(first, 3, 0), fixture.note(first, 2, 5)));
	}

	@Test
	public void decrementingLowersEverySelectedNote() {
		assertEquals("s2f28 s3f4 |  | success", decrement(fixture.note(first, 3, 5), fixture.note(first, 2, 29)));
	}

	@Test
	public void aNoteOnTheLastFretKeepsEveryNoteFromRising() {
		assertEquals("s2f5 s3f29 | ", increment(fixture.note(first, 3, 29), fixture.note(first, 2, 5)));
	}

	@Test
	public void anOpenStringNoteKeepsEveryNoteFromFalling() {
		assertEquals("s2f5 s3f0 | ", decrement(fixture.note(first, 2, 5), fixture.note(first, 3, 0)));
	}

	@Test
	public void notesOfSeveralBeatsMoveWithoutReportingSuccess() {
		assertEquals("s3f6 | s3f8", increment(fixture.note(first, 3, 5), fixture.note(second, 3, 7)));
	}

	@Test
	public void withoutSelectedNotesNothingMoves() {
		TGNote note = fixture.note(first, 3, 5);

		assertEquals("s3f5 | ", run(new TGIncrementNoteSemitoneAction(fixture.context)));
		assertEquals("s3f5 | ", increment());
		assertEquals("s3f6 |  | success", increment(note));
	}
}
