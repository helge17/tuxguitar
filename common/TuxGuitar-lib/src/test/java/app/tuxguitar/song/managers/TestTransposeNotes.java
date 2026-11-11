package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how the notes of a beat, and the chord diagram above it, are transposed across the strings of a guitar
 * in standard tuning (TGMeasureManager.transposeNotes): on the same string when asked and possible, else on the
 * next string toward the transposition (pushing on a note already there), else removed.
 *
 * The beat holds the notes and a chord diagram of the same frets; a result is "s<string>f<fret>" for each note,
 * and every scenario checks the chord diagram ends up the same as the notes.
 */
public class TestTransposeNotes {

	private static final int MAX_FRET = 12;

	private final TGSongManager songManager = new TGSongManager();
	private final TGFactory factory = songManager.getFactory();
	private final TGMeasureManager manager = songManager.getMeasureManager();
	private final TGMeasure measure = songManager.newSong().getTrack(0).getMeasure(0);
	private TGBeat beat;

	public TestTransposeNotes() {
		measure.getTrack().setMaxFret(MAX_FRET);
	}

	// notes as {string, fret} pairs, in one beat with the same chord diagram
	private void beat(int[]... notes) {
		beat = factory.newBeat();
		beat.setPreciseStart(measure.getPreciseStart());
		TGVoice voice = beat.getVoice(0);
		voice.setEmpty(false);
		voice.getDuration().setValue(TGDuration.QUARTER);
		TGChord chord = factory.newChord(6);
		for (int[] stringFret : notes) {
			TGNote note = factory.newNote();
			note.setString(stringFret[0]);
			note.setValue(stringFret[1]);
			voice.addNote(note);
			chord.addFretValue(stringFret[0] - 1, stringFret[1]);
		}
		chord.setFirstFret(3);
		chord.setName("C");
		beat.setChord(chord);
		manager.addBeat(measure, beat);
	}

	private static int[] at(int string, int fret) {
		return new int[] {string, fret};
	}

	private String transpose(int transposition, boolean tryKeepString) {
		manager.transposeNotes(measure, transposition, tryKeepString, true, -1);
		return notes();
	}

	private String notes() {
		List<TGNote> notes = new ArrayList<TGNote>(beat.getVoice(0).getNotes());
		notes.sort(Comparator.comparingInt(TGNote::getString));
		StringBuilder description = new StringBuilder();
		for (TGNote note : notes) {
			description.append(description.length() == 0 ? "" : " ").append("s" + note.getString() + "f" + note.getValue());
		}
		assertEquals(description.toString(), chord(), "the chord diagram follows the notes");
		return description.toString();
	}

	private String chord() {
		StringBuilder description = new StringBuilder();
		TGChord chord = beat.getChord();
		for (int i = 0; i < chord.countStrings(); i++) {
			if (chord.getFretValue(i) >= 0) {
				description.append(description.length() == 0 ? "" : " ").append("s" + (i + 1) + "f" + chord.getFretValue(i));
			}
		}
		return description.toString();
	}

	@Test
	public void aNoteKeepsItsStringWhenAsked() {
		beat(at(3, 5));
		assertEquals(""
			+ "s3f7"
			, transpose(2, true));
	}

	@Test
	public void aNoteMovesToTheNextStringWhenNotAsked() {
		beat(at(3, 5));
		assertEquals(""
			+ "s2f3"
			, transpose(2, false));
	}

	@Test
	public void aNoteBeyondTheLastFretMovesToAHigherString() {
		beat(at(3, 11));
		assertEquals(""
			+ "s2f10"
			, transpose(3, true));
	}

	@Test
	public void aNoteBelowTheNutMovesToALowerString() {
		beat(at(2, 0));
		assertEquals(""
			+ "s3f2"
			, transpose(-2, true));
	}

	@Test
	public void aNoteThatFitsNowhereIsRemoved() {
		beat(at(1, 11), at(3, 5));
		assertEquals(""
			+ "s3f8"
			, transpose(3, true));
	}

	@Test
	public void aNoteStaysOnItsStringWhenTheNextOneIsTaken() {
		beat(at(3, 5), at(2, 0));
		assertEquals(""
			+ "s2f2 s3f7"
			, transpose(2, false));
	}

	@Test
	public void aNoteInTheWayIsPushedToTheNextString() {
		beat(at(3, 11), at(2, 12));
		assertEquals(""
			+ "s1f10 s2f10"
			, transpose(3, true));
	}

	@Test
	public void aNoteInTheWayThatCannotMoveIsRemoved() {
		beat(at(3, 11), at(2, 2), at(1, 0));
		assertEquals(""
			+ "s1f3 s2f10"
			, transpose(3, true));
	}

	@Test
	public void aNoteFindsTheFirstFreeStringFurtherOn() {
		beat(at(4, 12), at(3, 4));
		assertEquals(""
			+ "s1f10 s2f12"
			, transpose(12, true));
	}

	@Test
	public void aWholeChordShapeMovesUp() {
		beat(at(1, 0), at(2, 1), at(3, 0), at(4, 2), at(5, 3));
		assertEquals(""
			+ "s1f2 s2f3 s3f2 s4f4 s5f5"
			, transpose(2, true));
		assertEquals(""
			+ "s1f8 s2f8 s3f9 s4f10"
			, transpose(10, false));
	}

	@Test
	public void onlyTheChosenStringIsTransposed() {
		beat(at(3, 5), at(2, 5));
		manager.transposeNotes(measure, 2, true, true, 3);
		assertEquals(""
			+ "s2f5 s3f7"
			, notes());
	}

	@Test
	public void theChordLosesItsNameAndFirstFret() {
		beat(at(3, 5));
		transpose(2, true);
		assertEquals("-1 ''", beat.getChord().getFirstFret() + " '" + beat.getChord().getName() + "'");
	}

	@Test
	public void theChordIsLeftAloneWhenNotAsked() {
		beat(at(3, 5));
		manager.transposeNotes(measure, 2, true, false, -1);
		assertEquals("s3f5 'C'", "s3f" + beat.getChord().getFretValue(2) + " '" + beat.getChord().getName() + "'");
	}

	// ---- each string by its own transposition (the overload taking one transposition per string) ----

	private String transposeStrings(int... transpositions) {
		manager.transposeNotes(measure, transpositions, true, true);
		return notes();
	}

	@Test
	public void eachStringMovesByItsOwnTransposition() {
		beat(at(1, 3), at(2, 3), at(3, 3));
		assertEquals("s1f4 s2f3 s3f1", transposeStrings(1, 0, -2, 0, 0, 0));
	}

	@Test
	public void stringsPastTheTranspositionsAreLeftAlone() {
		beat(at(1, 3), at(4, 3));
		assertEquals("s1f5 s4f3", transposeStrings(2, 2));
	}

	@Test
	public void aStringNoteThatCannotStayMovesToTheNextString() {
		beat(at(3, 1));
		assertEquals("s4f3", transposeStrings(0, 0, -3, 0, 0, 0));
	}

	@Test
	public void aStringNoteMovedOntoATransposedStringPushesItsNoteAway() {
		// string 2 goes 0 -> 2 first; then string 3 (11 + 5 is past the last fret) moves onto string 2 at fret 12,
		// pushing that note to string 1 below its open string: it is removed
		beat(at(3, 11), at(2, 0));
		assertEquals("s2f12", transposeStrings(0, 2, 5, 0, 0, 0));
	}

	@Test
	public void noTranspositionsLeaveTheNotesAlone() {
		beat(at(3, 5));
		assertEquals("s3f5", transposeStrings());
		assertEquals("s3f5", transposeStrings(0, 0, 0, 0, 0, 0));
		manager.transposeNotes(measure, null, true, true);
		assertEquals("s3f5", notes());
	}

	@Test
	public void theNotesOfAMeasureWithoutTrackStayPut() {
		TGMeasure trackless = factory.newMeasure(null);
		TGBeat lone = factory.newBeat();
		TGNote note = factory.newNote();
		note.setString(1);
		note.setValue(5);
		lone.getVoice(0).addNote(note);
		trackless.addBeat(lone);

		manager.transposeNotes(trackless, new int[] {1}, true, true);

		assertEquals("s1f5", "s" + note.getString() + "f" + note.getValue());
	}

	@Test
	public void aStringNoteLooksForAHigherStringByPitchNotByNumber() {
		// a ukulele: A4 E4 C4 G4, the 4th string sounding between the 2nd and the 1st
		int[] ukulele = {69, 64, 60, 67};
		List<TGString> strings = new ArrayList<TGString>();
		for (int i = 0; i < ukulele.length; i++) {
			TGString string = factory.newString();
			string.setNumber(i + 1);
			string.setValue(ukulele[i]);
			strings.add(string);
		}
		measure.getTrack().setStrings(strings);
		beat(at(4, 0));

		manager.transposeNotes(measure, new int[] {0, 0, 0, 2}, false, true);

		assertEquals("s1f0", notes());
	}
}
