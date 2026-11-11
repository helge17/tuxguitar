package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGNote;

/**
 * Pins how a note moves one semitone on its string (TGMeasureManager.canMoveSemitoneUp / moveSemitoneUp and the
 * Down pair): only to a playable fret, from the open string up to the last fret (a percussion track has no last
 * fret); "can" only tells, "move" also changes the fret.
 *
 * The track has 12 frets; a result is "answer: f<fret>" of the note.
 */
public class TestMoveSemitone {

	private final MeasureFixture fixture = new MeasureFixture();
	private final long start = fixture.beatAt(0).getStart();

	public TestMoveSemitone() {
		fixture.measure.getTrack().setMaxFret(12);
	}

	private String canUp(TGNote note) {
		return fixture.manager.canMoveSemitoneUp(fixture.measure, start, note.getString()) + ": f" + note.getValue();
	}

	private String up(TGNote note) {
		return fixture.manager.moveSemitoneUp(fixture.measure, start, note.getString()) + ": f" + note.getValue();
	}

	private String canDown(TGNote note) {
		return fixture.manager.canMoveSemitoneDown(fixture.measure, start, note.getString()) + ": f" + note.getValue();
	}

	private String down(TGNote note) {
		return fixture.manager.moveSemitoneDown(fixture.measure, start, note.getString()) + ": f" + note.getValue();
	}

	@Test
	public void movesUpAFret() {
		assertEquals("true: f6", up(fixture.fretted(3, 5)));
	}

	@Test
	public void movesDownAFret() {
		assertEquals("true: f4", down(fixture.fretted(3, 5)));
	}

	@Test
	public void askingLeavesTheNoteAlone() {
		TGNote note = fixture.fretted(3, 5);
		assertEquals("true: f5", canUp(note));
		assertEquals("true: f5", canDown(note));
	}

	@Test
	public void staysPutBelowTheOpenString() {
		TGNote note = fixture.fretted(3, 0);
		assertEquals("false: f0", canDown(note));
		assertEquals("false: f0", down(note));
	}

	@Test
	public void movesDownToTheOpenString() {
		assertEquals("true: f0", down(fixture.fretted(3, 1)));
	}

	@Test
	public void staysPutPastTheLastFret() {
		TGNote note = fixture.fretted(3, 12);
		assertEquals("false: f12", canUp(note));
		assertEquals("false: f12", up(note));
	}

	@Test
	public void movesUpToTheLastFret() {
		assertEquals("true: f12", up(fixture.fretted(3, 11)));
	}

	@Test
	public void aPercussionTrackHasNoLastFret() {
		fixture.percussion();
		assertEquals("true: f13", up(fixture.fretted(3, 12)));
	}

	@Test
	public void thereIsNothingToMoveWithoutANote() {
		fixture.fretted(3, 5);
		assertEquals(false, fixture.manager.moveSemitoneUp(fixture.measure, start, 2));
		assertEquals(false, fixture.manager.canMoveSemitoneDown(fixture.measure, start, 2));
	}
}
