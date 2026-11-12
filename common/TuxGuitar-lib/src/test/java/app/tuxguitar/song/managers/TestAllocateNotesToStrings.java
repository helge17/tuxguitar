package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;

/**
 * Pins how TGTrackManager.allocateNotesToStrings moves the notes of a beat to a new tuning: each note to the string
 * closest in pitch to its old one when the whole beat fits that way, else every note to the lowest fret on a free
 * string, highest pitch first, dropping the notes left without one.
 */
public class TestAllocateNotesToStrings {

	private static final int MAX_FRET = 29;

	private final TGFactory factory = new TGFactory();

	@Test
	public void noNoteMovesWhenTheTuningDidNotChange() {
		// fret 30 is above the highest fret: any allocation would drop the note
		assertEquals("s1f30", retune("s1f30", Arrays.asList(55, 50), MAX_FRET, 55, 50));
	}

	private static final List<Integer> STANDARD = Arrays.asList(64, 59, 55, 50, 45, 40);

	// a beat of notes "s<string>f<fret>[g<grace fret>]", voice 0 then voice 1 after a "|", retuned from
	// the open string values "from" to strings tuned to "to" (string 1 first); returns its notes in the same form
	private String retune(String notes, List<Integer> from, int maxFret, int... to) {
		String[] voices = notes.split("\\|");
		TGBeat beat = factory.newBeat();
		for (int v = 0; v < voices.length; v++) {
			for (String spec : voices[v].trim().split(" ")) {
				beat.getVoice(v).addNote(note(spec));
			}
		}
		List<TGString> toStrings = new ArrayList<TGString>();
		for (int i = 0; i < to.length; i++) {
			toStrings.add(TGSongManager.newString(factory, i + 1, to[i]));
		}
		new TGTrackManager(new TGSongManager()).allocateNotesToStrings(from, Arrays.asList(beat), toStrings, maxFret);

		List<String> result = new ArrayList<String>();
		for (int v = 0; v < voices.length; v++) {
			List<String> voice = new ArrayList<String>();
			for (TGNote note : beat.getVoice(v).getNotes()) {
				voice.add("s" + note.getString() + "f" + note.getValue() + (note.getEffect().isGrace() ? "g" + note.getEffect().getGrace().getFret() : ""));
			}
			result.add(String.join(" ", voice));
		}
		return String.join(" | ", result);
	}

	private TGNote note(String spec) {
		String[] values = spec.substring(1).split("[fg]");
		TGNote note = factory.newNote();
		note.setString(Integer.parseInt(values[0]));
		note.setValue(Integer.parseInt(values[1]));
		if (values.length > 2) {
			note.getEffect().setGrace(factory.newEffectGrace());
			note.getEffect().getGrace().setFret(Integer.parseInt(values[2]));
		}
		return note;
	}

	@Test
	public void eachNoteKeepsTheClosestStringInPitchWhenAllOfTheBeatFits() {
		assertEquals("s1f5", retune("s1f0", Arrays.asList(45), MAX_FRET, 40));
		// a half step down: same strings, one fret higher, grace notes too, across voices
		assertEquals("s1f1 s2f2 s6f4", retune("s1f0 s2f1 s6f3", STANDARD, MAX_FRET, 63, 58, 54, 49, 44, 39));
		assertEquals("s3f3g1", retune("s3f2g0", STANDARD, MAX_FRET, 63, 58, 54, 49, 44, 39));
		assertEquals("s1f1 | s2f1", retune("s1f0 | s2f0", STANDARD, MAX_FRET, 63, 58, 54, 49, 44, 39));
		// a half step up, while every fret stays on the neck
		assertEquals("s1f0 s2f2", retune("s1f1 s2f3", STANDARD, MAX_FRET, 65, 60, 56, 51, 46, 41));
		// the highest fret allowed still fits, for the note and for its grace note
		assertEquals("s1f6", retune("s1f5", STANDARD, 6, 63, 58, 54, 49, 44, 39));
		assertEquals("s1f3g6", retune("s1f2g5", STANDARD, 6, 63, 58, 54, 49, 44, 39));
	}

	@Test
	public void theWholeBeatGoesToTheLowestFretsWhenANoteDoesNotFitItsClosestString() {
		// the open string would need fret -1: highest pitch first, each on the free string of its lowest fret
		assertEquals("s2f4 s3f4", retune("s1f0 s2f1", STANDARD, MAX_FRET, 65, 60, 56, 51, 46, 41));
		// the grace note would need fret -1
		assertEquals("s2f6g4", retune("s1f2g0", STANDARD, MAX_FRET, 65, 60, 56, 51, 46, 41));
		// the note would need fret 7, above the highest fret
		assertEquals("s2f4", retune("s1f6", STANDARD, 6, 63, 66));
		// the grace note would need fret 7, and has no place on the lowest frets either: the note is dropped
		assertEquals("", retune("s1f2g6", STANDARD, 6, 63, 58, 54, 49, 44, 39));
		// the closest string of the second note is taken by the first, whatever the order of the notes
		assertEquals("s1f0 s2f23", retune("s1f0 s2f0", Arrays.asList(64, 63), MAX_FRET, 64, 40));
		assertEquals("s1f10 s2f12", retune("s1f0 s2f2", Arrays.asList(55, 50), MAX_FRET, 45, 40));
		assertEquals("s1f10 s2f12", retune("s2f2 s1f0", Arrays.asList(55, 50), MAX_FRET, 45, 40));
		// with fewer strings, the lowest note is left without one
		assertEquals("s1f10", retune("s1f0 s2f2", Arrays.asList(55, 50), MAX_FRET, 45));
		// a note of voice 1 cannot take its string: voice 0 is allocated again from its original fret
		assertEquals("s1f1 | s2f11", retune("s1f0 | s1f5", STANDARD, MAX_FRET, 63, 58, 54, 49, 44, 39));
	}

	@Test
	public void withAnOpenStringOfPitchZeroNotesGoToTheLowestFretsAndThoseWithoutAFreeStringAreDropped() {
		// an A major chord, all on that string, is distributed over the strings
		assertEquals("s1f5 s2f5 s3f6 s4f7 s5f7 s6f5",
			retune("s1f69 s1f64 s1f61 s1f57 s1f52 s1f45", Arrays.asList(0), MAX_FRET, 64, 59, 55, 50, 45, 40));
		// pitch 70 is above fret 5 of every string; pitch 66 takes fret 2 of string 1
		assertEquals("s1f2", retune("s1f70 s1f66", Arrays.asList(0), 5, 64, 59));
		// fret 5 is still allowed
		assertEquals("s1f5", retune("s1f69", Arrays.asList(0), 5, 64, 59));
		// its grace note (pitch 62) is below string 1, so the note goes to string 2
		assertEquals("s2f7g3", retune("s1f66g62", Arrays.asList(0), MAX_FRET, 64, 59));
		// nor above the highest fret
		assertEquals("s1f0g6", retune("s1f64g70", Arrays.asList(0), 6, 64));
		assertEquals("", retune("s1f64g71", Arrays.asList(0), 6, 64));
	}
}
