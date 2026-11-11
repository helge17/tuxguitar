package app.tuxguitar.editor.action.note;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;

/**
 * Pins TGInsertChordAction: the chord replaces the notes of the caret voice with one note per string the chord frets
 * (fret 0 included), at the caret velocity, and is attached to the beat. Inserting the diagram only attaches a chord
 * that frets strings and leaves the notes alone. A chord that frets no string is neither attached to a rest beat nor
 * inserted as a diagram only; on a beat with notes it clears them and is attached.
 *
 * The beat reads back as its voice 0 notes, then "+chord" when a chord is attached.
 */
public class TestInsertChordAction {

	private static final int CARET_VELOCITY = 47;

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGBeat beat = fixture.beat(fixture.song.getTrack(0).getMeasure(0), 0);
	private final TGChord chord = fixture.songManager.getFactory().newChord(6);

	public TestInsertChordAction() {
		fixture.caret(beat, 1);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, CARET_VELOCITY);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
	}

	// the chord frets string 1 open
	private TestInsertChordAction withOpenStringChord() {
		chord.addFretValue(0, 0);
		return this;
	}

	// the chord frets string 1 open and string 3 at fret 2
	private TestInsertChordAction withFrettedChord() {
		chord.addFretValue(2, 2);
		return withOpenStringChord();
	}

	private TestInsertChordAction withNoteOnString(int string) {
		fixture.note(beat, string, 5);
		return this;
	}

	private TestInsertChordAction diagramOnly() {
		fixture.actionContext.setAttribute(TGInsertChordAction.ATTRIBUTE_CHORD_INSERT_DIAGRAM_ONLY, Boolean.TRUE);
		return this;
	}

	private String insertChord() {
		fixture.execute(new TGInsertChordAction(fixture.context));
		return SongActionFixture.describe(beat) + (beat.getChord() == chord ? " +chord" : "");
	}

	@TestFactory
	public List<DynamicTest> insertingTheChord() {
		return Arrays.asList(
			inserting("the chord replaces the notes", "s1f0 s3f2 +chord",
					test -> test.withFrettedChord().withNoteOnString(2).insertChord()),
			inserting("the chord fills a rest beat", "s1f0 s3f2 +chord",
					test -> test.withFrettedChord().insertChord()),
			inserting("the diagram only leaves the notes alone", "s2f5 +chord",
					test -> test.withFrettedChord().withNoteOnString(2).diagramOnly().insertChord()),
			inserting("a chord of one string fills a rest beat", "s1f0 +chord",
					test -> test.withOpenStringChord().insertChord()),
			inserting("the diagram only of one string leaves the notes alone", "s2f5 +chord",
					test -> test.withOpenStringChord().withNoteOnString(2).diagramOnly().insertChord()),
			inserting("a chord without frets clears the notes", " +chord",
					test -> test.withNoteOnString(2).insertChord()),
			inserting("a chord without frets is a diagram that clears the notes too", " +chord",
					test -> test.withNoteOnString(2).diagramOnly().insertChord()),
			inserting("a chord without frets leaves a rest beat alone", "",
					test -> test.insertChord()),
			inserting("a diagram without frets leaves a rest beat alone", "",
					test -> test.diagramOnly().insertChord()));
	}

	// a case run on a new beat: inserting the chord gives the beat read back
	private static DynamicTest inserting(String name, String expected, Function<TestInsertChordAction, String> insertChord) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(expected, insertChord.apply(new TestInsertChordAction())));
	}

	@Test
	public void theChordNotesTakeTheCaretVelocityInTheCaretVoice() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, beat.getVoice(1));
		withFrettedChord().withNoteOnString(2).insertChord();

		assertEquals("s2f5 | 1v47 3v47", SongActionFixture.describe(beat) + " | " + velocities(beat, 1));
	}

	private static String velocities(TGBeat beat, int voice) {
		StringBuilder notes = new StringBuilder();
		beat.getVoice(voice).getNotes().forEach(note -> notes.append(" ").append(note.getString()).append("v").append(note.getVelocity()));
		return notes.toString().trim();
	}
}
