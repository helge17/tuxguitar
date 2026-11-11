package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins TGMeasureManager.addNote with a duration, the way the note editor calls it: at a tick position of a 4/4
 * measure, in a voice. The layout of voice 0 is MeasureFixture's; a note is described as "s<string>@<position>"
 * of the beat that received it, position in quarters, or "-" when it was not added.
 */
public class TestAddNote {

	private static final long QUARTER_TICKS = TGDuration.QUARTER_TIME;

	private final MeasureFixture fixture = new MeasureFixture();
	private final TGBeat first = fixture.rest(0, TGDuration.WHOLE, 0).getBeat();

	private TGNote newNote(int string) {
		TGNote note = fixture.factory.newNote();
		note.setString(string);
		return note;
	}

	// adds a note on "string" at "quarters" from the measure start, in voice "voiceIndex"
	private TGNote addAt(double quarters, int string, TGDuration duration, int voiceIndex) {
		TGNote note = newNote(string);
		long start = first.getStart() + (long) (quarters * QUARTER_TICKS);
		fixture.manager.addNote(fixture.measure, start, note, duration, voiceIndex);
		return note;
	}

	private String where(TGNote note) {
		TGVoice voice = note.getVoice();
		if (voice == null) {
			return "-";
		}
		double position = (voice.getBeat().getPreciseStart() - fixture.measure.getPreciseStart()) / (double) MeasureFixture.QUARTER;
		return "s" + note.getString() + "@" + position + " v" + voice.getIndex();
	}

	// the whole rest is cut to a quarter at the note's position, and the rest of the measure filled with rests
	@Test
	public void theNoteTakesItsDurationAtItsPositionInsideTheBeat() {
		TGNote inside = addAt(2, 1, fixture.duration(TGDuration.QUARTER), 0);
		String afterInside = fixture.layout() + " | " + where(inside);
		TGNote atStart = addAt(0, 2, fixture.duration(TGDuration.QUARTER), 0);

		assertEquals("r@0 r@1 n@2 r@3 | s1@2.0 v0 ; n@0 r@1 n@2 r@3 | s2@0.0 v0", afterInside + " ; " + fixture.layout() + " | " + where(atStart));
	}

	@Test
	public void aNoteOnTheSameStringInAnotherVoiceIsReplaced() {
		TGVoice other = fixture.note(0, TGDuration.QUARTER, 1);
		TGNote note = addAt(0, 1, fixture.duration(TGDuration.QUARTER), 0);
		assertEquals("n@0 r@1 r@2 r@3 | s1@0.0 v0 | 0", fixture.layout() + " | " + where(note) + " | " + other.countNotes());
	}

	@Test
	public void aPositionNoVoiceStartsAtKeepsTheNoteOnTheGivenBeat() {
		TGNote note = newNote(3);
		fixture.manager.addNote(first, note, fixture.duration(TGDuration.HALF), first.getStart() + 100 * QUARTER_TICKS, 0);
		assertEquals("n@0 r@2 | s3@0.0 v0", fixture.layout() + " | " + where(note));
	}

	@Test
	public void aPositionOutsideTheMeasureAddsNothing() {
		TGNote outside = addAt(5, 1, fixture.duration(TGDuration.QUARTER), 0);
		TGNote inside = addAt(3, 1, fixture.duration(TGDuration.QUARTER), 0);
		assertEquals("- | s1@3.0 v0", where(outside) + " | " + where(inside));
	}

	// voice 1 holds a 64th note at a 64th from the start: a nonuplet 64th at the start would leave a gap that
	// cannot be written, so it is refused, and the voice is left empty or not as it was
	@Test
	public void aDurationLeavingAnUnwritableGapIsRefused() {
		fixture.note(TGDuration.WHOLE_PRECISE_DURATION / 64, TGDuration.SIXTY_FOURTH, 1);
		TGDuration nonuplet = fixture.duration(TGDuration.SIXTY_FOURTH);
		nonuplet.getDivision().setEnters(9);
		nonuplet.getDivision().setTimes(8);

		String onEmptyVoice = where(addAt(0, 2, nonuplet, 1)) + " " + first.getVoice(1).isEmpty();
		String accepted = where(addAt(0, 2, fixture.duration(TGDuration.SIXTY_FOURTH), 1)) + " " + first.getVoice(1).isEmpty();
		String onNoteVoice = where(addAt(0, 3, nonuplet, 1)) + " " + first.getVoice(1).isEmpty();

		assertEquals("- true | s2@0.0 v1 false | - false", onEmptyVoice + " | " + accepted + " | " + onNoteVoice);
	}

	@Test
	public void aNoteOnTheSameStringAtTheBeatIsReplacedAndTheStrokeKept() {
		TGVoice voice = fixture.note(0, TGDuration.QUARTER, 0);
		first.getStroke().setDirection(TGStroke.STROKE_UP);
		TGNote note = addAt(0, 1, fixture.duration(TGDuration.QUARTER), 0);
		assertEquals("1 " + where(note) + " " + TGStroke.STROKE_UP, voice.countNotes() + " " + where(voice.getNote(0)) + " " + first.getStroke().getDirection());
	}

	@Test
	public void aLongerNoteShiftsTheFollowingNotesIntoTheTrailingRests() {
		fixture.quarterNotes(0, 1);
		fixture.rest(2 * MeasureFixture.QUARTER, TGDuration.HALF, 0);
		TGNote note = addAt(0, 2, fixture.duration(TGDuration.HALF), 0);
		assertEquals("n@0 n@2 | s2@0.0 v0 2", fixture.layout() + " | " + where(note) + " " + note.getVoice().getDuration().getValue());
	}

	@Test
	public void aLongerNoteWithNoRoomTakesTheDurationOfTheBeat() {
		fixture.quarterNotes(0, 1, 2, 3);
		TGNote note = addAt(0, 2, fixture.duration(TGDuration.HALF), 0);
		assertEquals("n@0 n@1 n@2 n@3 | s2@0.0 v0 4", fixture.layout() + " | " + where(note) + " " + note.getVoice().getDuration().getValue());
	}
}
