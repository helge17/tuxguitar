package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.helpers.TGMeasureError;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins the small edits TGMeasureManager makes on the beats, voices and notes of a 4/4 measure: adding and removing
 * notes, beats and voices, toggling pick strokes and ties, cleaning beats, and fixing a voice by its error code.
 * Positions are in quarters from the measure start; a note is "s<string>f<fret>", "~" when tied.
 */
public class TestMeasureEdits {

	private final MeasureFixture fixture = new MeasureFixture();
	private final TGMeasureManager manager = fixture.manager;

	private List<TGBeat> beatsInOrder() {
		List<TGBeat> beats = new ArrayList<TGBeat>(fixture.measure.getBeats());
		beats.sort(Comparator.comparingLong(TGBeat::getPreciseStart));
		return beats;
	}

	private long position(TGBeat beat) {
		return (beat.getPreciseStart() - fixture.measure.getPreciseStart()) / QUARTER;
	}

	private static String describe(List<TGNote> notes) {
		List<String> described = new ArrayList<String>();
		for (TGNote note : notes) {
			described.add("s" + note.getString() + "f" + note.getValue() + (note.isTiedNote() ? "~" : ""));
		}
		return String.join(" ", described);
	}

	// "position:<voice 0 notes>|<voice 1 notes>" per beat; a rest voice is "r", an empty voice "_"
	private String voices() {
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : beatsInOrder()) {
			beats.add(position(beat) + ":" + voice(beat.getVoice(0)) + "|" + voice(beat.getVoice(1)));
		}
		return String.join(" ", beats);
	}

	private static String voice(TGVoice voice) {
		if (voice.isEmpty()) {
			return "_";
		}
		return voice.isRestVoice() ? "r" + voice.getDuration().getValue() : describe(voice.getNotes()) + "/" + voice.getDuration().getValue();
	}

	// "position:<duration value of voice 0>" per beat
	private String durations() {
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : beatsInOrder()) {
			beats.add(position(beat) + ":" + beat.getVoice(0).getDuration().getValue());
		}
		return String.join(" ", beats);
	}

	private TGNote newNote(int string, int fret) {
		TGNote note = fixture.factory.newNote();
		note.setString(string);
		note.setValue(fret);
		return note;
	}

	// a quarter note s1f0 at each quarter: a full, valid measure
	private void fullMeasure() {
		fixture.quarterNotes(0, 1, 2, 3);
	}

	// --- notes ---

	@Test
	public void addingANoteToAVoiceReplacesTheNoteOnItsString() {
		fixture.fretted(0, 1, 3, 0);
		fixture.fretted(0, 2, 5, 0);
		fixture.fretted(0, 1, 4, 1);
		TGBeat beat = fixture.beatAt(0);

		manager.addNote(beat, newNote(1, 7), 0);
		manager.addNote(beat, newNote(3, 2), 0);

		// only the note of voice 0 on string 1 is replaced; a free string just gets the note
		assertEquals("s2f5 s1f7 s3f2 | s1f4", describe(beat.getVoice(0).getNotes()) + " | " + describe(beat.getVoice(1).getNotes()));
	}

	// the chord and strokes of a beat whose only note is replaced
	private String keptOnReplace(TGBeat beat) {
		return voices() + " chord " + beat.isChordBeat() + " stroke " + beat.getStroke().getDirection();
	}

	private TGBeat beatWithOneNoteAChordAndAStroke() {
		fixture.fretted(0, 1, 3, 0);
		TGBeat beat = fixture.beatAt(0);
		beat.setChord(fixture.factory.newChord(6));
		beat.getStroke().setDirection(TGStroke.STROKE_UP);
		return beat;
	}

	@Test
	public void replacingTheOnlyNoteOfABeatWithOrWithoutControlKeepsItsChordAndStroke() {
		TGBeat beat = beatWithOneNoteAChordAndAStroke();

		manager.addNote(beat, newNote(1, 7), 0);
		String withControl = keptOnReplace(beat);
		manager.addNoteWithoutControl(beat, newNote(1, 8), fixture.duration(TGDuration.HALF), 0);

		assertEquals("0:s1f7/4|_ chord true stroke 1 ; 0:s1f8/2|_ chord true stroke 1", withControl + " ; " + keptOnReplace(beat));
	}

	@Test
	public void addingANoteWithoutControlTakesTheStringFromEveryVoiceKeepsTheOtherNotesAndSetsTheDuration() {
		fixture.note(0, TGDuration.QUARTER, 1);
		fixture.fretted(0, 2, 5, 1);
		fixture.fretted(QUARTER, 2, 5, 0);

		manager.addNoteWithoutControl(fixture.beatAt(0), newNote(1, 9), fixture.duration(TGDuration.HALF), 0);
		manager.addNoteWithoutControl(fixture.beatAt(QUARTER), newNote(1, 9), fixture.duration(TGDuration.EIGHTH), 0);

		assertEquals("0:s1f9/2|s2f5/4 1:s2f5 s1f9/8|_", voices());
	}

	@Test
	public void theNotesAtAPositionAreThoseOfEveryVoiceOfTheBeatStartingThere() {
		fixture.fretted(0, 1, 3, 0);
		fixture.fretted(0, 2, 5, 1);
		fixture.fretted(QUARTER, 3, 7, 0);
		long start = fixture.beatAt(0).getStart();

		String atStart = describe(manager.getNotes(fixture.measure, start));
		String between = describe(manager.getNotes(fixture.measure, start + 1));

		assertEquals("s1f3 s2f5 | ", atStart + " | " + between);
	}

	@Test
	public void cleaningAVoiceRemovesItsNotesAndTheStrokesOfTheBeatLeftWithRests() {
		fixture.fretted(0, 1, 3, 0);
		fixture.fretted(0, 2, 5, 0);
		TGBeat beat = fixture.beatAt(0);
		beat.getStroke().setDirection(TGStroke.STROKE_UP);

		manager.cleanVoiceNotes(beat.getVoice(0));
		manager.cleanVoiceNotes(beat.getVoice(1));

		assertEquals("0:r4|_ stroke " + TGStroke.STROKE_NONE, voices() + " stroke " + beat.getStroke().getDirection());
	}

	@Test
	public void cleaningABeatLeavesRestsWithoutStrokesTextOrChord() {
		fixture.fretted(0, 1, 3, 0);
		fixture.fretted(0, 2, 5, 1);
		TGBeat beat = fixture.beatAt(0);
		beat.getStroke().setDirection(TGStroke.STROKE_DOWN);
		beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_UP);
		TGText text = fixture.factory.newText();
		text.setValue("text");
		beat.setText(text);
		beat.setChord(fixture.factory.newChord(6));

		manager.cleanBeat(beat);

		assertEquals("0:r4|r4 stroke 0 pick 0 text false chord false", voices()
			+ " stroke " + beat.getStroke().getDirection() + " pick " + beat.getPickStroke().getDirection()
			+ " text " + beat.isTextBeat() + " chord " + beat.isChordBeat());
	}

	@Test
	public void cleaningARestBeatRemovesItsChord() {
		fixture.rest(0, TGDuration.QUARTER, 0);
		TGBeat beat = fixture.beatAt(0);
		beat.setChord(fixture.factory.newChord(6));

		manager.cleanBeat(beat);

		assertEquals(false, beat.isChordBeat());
	}

	@Test
	public void cleaningABeatWithoutTextOrChordLeavesNone() {
		fixture.fretted(0, 1, 3, 0);
		TGBeat beat = fixture.beatAt(0);

		manager.cleanBeat(beat);

		assertEquals("0:r4|_ text false chord false", voices() + " text " + beat.isTextBeat() + " chord " + beat.isChordBeat());
	}

	// --- ties ---

	private TGNote noteAt(int quarter) {
		return fixture.beatAt(quarter * QUARTER).getVoice(0).getNote(0);
	}

	// toggles the tie of the dead note at quarter 1 of a full measure of s1f0 notes, once given "fret" and "tied"
	private static String tieToggled(int fret, boolean tied, boolean freeEditionMode) {
		TestMeasureEdits test = new TestMeasureEdits();
		test.fullMeasure();
		TGNote note = test.noteAt(1);
		note.setValue(fret);
		note.setTiedNote(tied);
		note.getEffect().setDeadNote(true);
		test.fixture.songManager.setFreeEditionMode(freeEditionMode);

		test.manager.changeTieNote(note);

		return "tied " + note.isTiedNote() + " dead " + note.getEffect().isDeadNote();
	}

	@Test
	public void aNoteIsTiedToAPreviousNoteOfItsStringAndFretOrInFreeEditionModeAndATiedNoteIsUntied() {
		assertEquals(String.join("\n",
			// the previous note has the same string and fret: tied, no longer dead
			"tied true dead false",
			// another fret: left alone
			"tied false dead true",
			// in free edition mode, any note
			"tied true dead false",
			// a tied note is untied, whatever the previous note
			"tied false dead false"),
			String.join("\n", tieToggled(0, false, false), tieToggled(2, false, false), tieToggled(2, false, true), tieToggled(2, true, false)));
	}

	// --- pick strokes ---

	@Test
	public void aPickStrokeUpIsToggledAndReplacesAPickStrokeDown() {
		fullMeasure();
		TGBeat beat = fixture.beatAt(0);
		long start = beat.getStart();
		List<String> directions = new ArrayList<String>();

		for (int i = 0; i < 2; i++) {
			directions.add(manager.changePickStrokeUp(fixture.measure, start) + ":" + beat.getPickStroke().getDirection());
		}
		beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_DOWN);
		directions.add(manager.changePickStrokeUp(fixture.measure, start) + ":" + beat.getPickStroke().getDirection());
		directions.add(manager.changePickStrokeUp(fixture.measure, start + 1) + ":" + beat.getPickStroke().getDirection());

		assertEquals("true:1 true:0 true:1 false:1", String.join(" ", directions));
	}

	@Test
	public void aPickStrokeDownIsToggledAndReplacesAPickStrokeUp() {
		fullMeasure();
		TGBeat beat = fixture.beatAt(0);
		long start = beat.getStart();
		List<String> directions = new ArrayList<String>();

		for (int i = 0; i < 2; i++) {
			directions.add(manager.changePickStrokeDown(fixture.measure, start) + ":" + beat.getPickStroke().getDirection());
		}
		beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_UP);
		directions.add(manager.changePickStrokeDown(fixture.measure, start) + ":" + beat.getPickStroke().getDirection());
		directions.add(manager.changePickStrokeDown(fixture.measure, start + 1) + ":" + beat.getPickStroke().getDirection());

		assertEquals("true:-1 true:0 true:-1 false:-1", String.join(" ", directions));
	}

	// --- beats ---

	@Test
	public void theBeatsBeforeTheEndAreThoseStartingAtOrAfterAPosition() {
		fullMeasure();
		long second = fixture.beatAt(QUARTER).getStart();
		List<String> positions = new ArrayList<String>();

		for (long from : new long[] { second - 1, second, second + 1 }) {
			List<String> found = new ArrayList<String>();
			for (TGBeat beat : manager.getBeatsBeforeEnd(beatsInOrder(), from)) {
				found.add(String.valueOf(position(beat)));
			}
			positions.add(String.join(",", found));
		}

		assertEquals("1,2,3 1,2,3 2,3", String.join(" ", positions));
	}

	@Test
	public void removingABeatLeavesAHoleUnlessTheNextBeatsMoveBackLeavingARestAtTheEnd() {
		fullMeasure();
		manager.removeBeat(fixture.beatAt(QUARTER), false);
		String kept = fixture.layout();

		// the beat at 3 moves back to 2 (up to the next beat, not to the hole at 1)
		manager.removeBeat(fixture.beatAt(2 * QUARTER), true);

		assertEquals("n@0 n@2 n@3 ; n@0 n@2 r@3", kept + " ; " + fixture.layout());
	}

	@Test
	public void removingABeatAndMovingLeavesTheBeatsBeforeItInPlace() {
		fullMeasure();

		manager.removeBeat(fixture.beatAt(QUARTER), true);

		assertEquals("n@0 n@1 n@2 r@3", fixture.layout());
	}

	@Test
	public void removingTheLastBeatAndMovingLeavesARestOfItsShortestVoice() {
		fixture.note(0, TGDuration.HALF, 0);
		fixture.note(2 * QUARTER, TGDuration.HALF, 0);
		fixture.note(2 * QUARTER, TGDuration.QUARTER, 1);

		manager.removeBeat(fixture.beatAt(2 * QUARTER), true);

		assertEquals("n@0 r@2 | 0:2 2:4", fixture.layout() + " | " + durations());
	}

	// --- voices ---

	@Test
	public void removingAVoiceRemovesItsBeatOnlyWhenNoOtherVoiceIsLeft() {
		fixture.note(0, TGDuration.QUARTER, 0);
		fixture.note(0, TGDuration.QUARTER, 1);
		fixture.note(QUARTER, TGDuration.QUARTER, 0);

		manager.removeVoice(fixture.beatAt(0).getVoice(0));
		manager.removeVoice(fixture.beatAt(QUARTER).getVoice(0));

		assertEquals("0:_|s1f0/4", voices());
	}

	@Test
	public void removingAVoiceAndMovingShiftsTheNextVoicesBackUpToItLeavingARestAtTheEnd() {
		fullMeasure();
		manager.removeVoice(fixture.beatAt(QUARTER).getVoice(0), false);
		String kept = voices();

		manager.removeVoice(fixture.beatAt(2 * QUARTER).getVoice(0), true);

		assertEquals("0:s1f0/4|_ 2:s1f0/4|_ 3:s1f0/4|_ ; 0:s1f0/4|_ 2:s1f0/4|_ 3:r4|_", kept + " ; " + voices());
	}

	@Test
	public void removingTheLastVoiceAndMovingLeavesARestInItsPlace() {
		fixture.quarterNotes(0, 1);
		fixture.note(2 * QUARTER, TGDuration.HALF, 0);

		manager.removeVoice(fixture.beatAt(2 * QUARTER).getVoice(0), true);

		assertEquals("0:s1f0/4|_ 1:s1f0/4|_ 2:r2|_", voices());
	}

	// --- fixing a voice by its error code ---

	// half notes at quarters 0, 1 and 3: the first overlaps the second, the last ends after the measure
	private void overlappingAndTooLong() {
		fixture.note(0, TGDuration.HALF, 0);
		fixture.note(QUARTER, TGDuration.HALF, 0);
		fixture.note(3 * QUARTER, TGDuration.HALF, 0);
	}

	private String fixedWith(int errCode) {
		overlappingAndTooLong();
		manager.fixVoice(fixture.measure, 0, errCode);
		return durations();
	}

	@Test
	public void aVoiceIsFixedOnlyForTheErrorsItsCodeNames() {
		List<String> fixes = new ArrayList<String>();
		int[] codes = {
			TGMeasureError.OK,
			TGMeasureError.VOICE_OVERLAP,
			TGMeasureError.VOICE_TOO_LONG,
			TGMeasureError.VOICE_TOO_SHORT,
			TGMeasureError.VOICE_OVERLAP | TGMeasureError.VOICE_TOO_LONG,
		};
		for (int code : codes) {
			fixes.add(new TestMeasureEdits().fixedWith(code));
		}

		assertEquals(String.join("\n",
			"0:2 1:2 3:2",
			"0:4 1:2 3:2",
			"0:2 1:2 3:4",
			"0:2 1:2 3:4",
			"0:4 1:2 3:4"), String.join("\n", fixes));
	}
}
