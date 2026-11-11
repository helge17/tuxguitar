package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectGrace;

/**
 * Pins how a note moves to the next string up (toward string 1) or down, keeping its pitch
 * (TGMeasureManager.shiftNoteUp / shiftNoteDown): only to a free string of the track, where the note and its grace
 * note stay between the open string and the last fret (a percussion track has no last fret).
 *
 * The track is a guitar in standard tuning (string 1 = E4 ... string 6 = E2) with 12 frets; a result is
 * "returned value: s<string>f<fret>" of the note, plus "g<fret>" of its grace note.
 */
public class TestShiftNote {

	private final MeasureFixture fixture = new MeasureFixture();
	private final TGBeat beat = fixture.beatAt(0);

	public TestShiftNote() {
		fixture.measure.getTrack().setMaxFret(12);
	}

	private TGNote note(int string, int fret) {
		return fixture.fretted(string, fret);
	}

	private TGNote note(int string, int fret, int graceFret) {
		TGNote note = note(string, fret);
		TGEffectGrace grace = fixture.factory.newEffectGrace();
		grace.setFret(graceFret);
		note.getEffect().setGrace(grace);
		return note;
	}

	private static String describe(int result, TGNote note) {
		String grace = note.getEffect().isGrace() ? "g" + note.getEffect().getGrace().getFret() : "";
		return result + ": s" + note.getString() + "f" + note.getValue() + grace;
	}

	private String up(TGNote note) {
		return describe(fixture.manager.shiftNoteUp(fixture.measure, beat.getStart(), note.getString()), note);
	}

	private String down(TGNote note) {
		return describe(fixture.manager.shiftNoteDown(fixture.measure, beat.getStart(), note.getString()), note);
	}

	@Test
	public void movesUpKeepingThePitch() {
		assertEquals("2: s2f1", up(note(3, 5)));
	}

	@Test
	public void movesDownKeepingThePitch() {
		assertEquals("4: s4f10", down(note(3, 5)));
	}

	@Test
	public void movesTheGraceNoteAlong() {
		assertEquals("2: s2f1g3", up(note(3, 5, 7)));
	}

	@Test
	public void thereIsNothingToMoveWithoutANote() {
		note(3, 5);
		assertEquals(0, fixture.manager.shiftNoteUp(fixture.measure, beat.getStart(), 2));
	}

	@Test
	public void staysPutPastTheOuterStrings() {
		assertEquals("0: s1f5", up(note(1, 5)));
		assertEquals("0: s6f5", down(note(6, 5)));
	}

	@Test
	public void staysPutWhenTheNextStringIsTaken() {
		note(2, 0);
		assertEquals("0: s3f5", up(note(3, 5)));
	}

	@Test
	public void staysPutBelowTheOpenString() {
		assertEquals("0: s3f3", up(note(3, 3)));
	}

	@Test
	public void staysPutPastTheLastFret() {
		assertEquals("0: s3f12", down(note(3, 12)));
	}

	@Test
	public void movesUpToTheLastFret() {
		assertEquals("4: s4f12", down(note(3, 7)));
	}

	@Test
	public void staysPutWhenTheGraceNoteWouldFallBelowTheOpenString() {
		assertEquals("0: s3f5g3", up(note(3, 5, 3)));
	}

	@Test
	public void staysPutWhenTheGraceNoteWouldPassTheLastFret() {
		assertEquals("0: s3f5g12", down(note(3, 5, 12)));
	}

	@Test
	public void movesTheGraceNoteUpToTheLastFret() {
		assertEquals("4: s4f10g12", down(note(3, 5, 7)));
	}

	@Test
	public void aPercussionTrackHasNoLastFret() {
		fixture.percussion();
		assertEquals("4: s4f17", down(note(3, 12)));
		assertEquals("0: s2f3", up(note(2, 3)));
	}

	@Test
	public void aPercussionGraceNoteHasNoLastFret() {
		fixture.percussion();
		assertEquals("4: s4f10g17", down(note(3, 5, 12)));
	}
}
