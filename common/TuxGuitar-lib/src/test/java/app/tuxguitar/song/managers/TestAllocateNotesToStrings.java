package app.tuxguitar.song.managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGVoice;

public class TestAllocateNotesToStrings {

	private static final int MAX_FRET = 29;

	private TGString stringE2;
	private TGString stringA2;
	private TGString stringD3;
	private TGString stringG3;
	private TGString stringB3;
	private TGString stringE4;
	private TGFactory factory;

	public TestAllocateNotesToStrings() {
		factory = new TGFactory();
		stringE2 = factory.newString();
		stringE2.setValue(40);
		stringA2 = factory.newString();
		stringA2.setValue(45);
		stringD3 = factory.newString();
		stringD3.setValue(50);
		stringG3 = factory.newString();
		stringG3.setValue(55);
		stringB3 = factory.newString();
		stringB3.setValue(59);
		stringE4 = factory.newString();
		stringE4.setValue(64);
	}

	@Test
	public void testMove1note() {
		TGTrackManager trackManager = new TGTrackManager(new TGSongManager());

		List<Integer> fromStrings = new ArrayList<Integer>();
		fromStrings.add(stringA2.getValue());

		TGBeat beat = factory.newBeat();
		TGNote noteA2 = factory.newNote();
		noteA2.setString(1);
		noteA2.setValue(0);
		beat.getVoice(0).addNote(noteA2);
		List<TGBeat> beats = new ArrayList<TGBeat>();
		beats.add(beat);

		stringE2.setNumber(1);
		List<TGString> toStrings = new ArrayList<TGString>();
		toStrings.add(stringE2);

		trackManager.allocateNotesToStrings(fromStrings, beats, toStrings, MAX_FRET);

		assertEquals(1, beats.get(0).getVoice(0).countNotes());
		assertEquals(1, beats.get(0).getVoice(0).getNote(0).getString());
		assertEquals(5, beats.get(0).getVoice(0).getNote(0).getValue());
	}

	@Test
	public void testMove1chord() {
		TGTrackManager trackManager = new TGTrackManager(new TGSongManager());

		List<Integer> fromStrings = new ArrayList<Integer>();
		fromStrings.add(stringG3.getValue());
		fromStrings.add(stringD3.getValue());

		TGBeat beat = factory.newBeat();
		TGNote noteG3 = factory.newNote();
		noteG3.setString(1);
		noteG3.setValue(0);
		beat.getVoice(0).addNote(noteG3);
		TGNote noteE3 = factory.newNote();
		noteE3.setString(2);
		noteE3.setValue(2);
		beat.getVoice(0).addNote(noteE3);

		List<TGBeat> beats = new ArrayList<TGBeat>();
		beats.add(beat);

		stringA2.setNumber(1);
		stringE2.setNumber(2);
		List<TGString> toStrings = new ArrayList<TGString>();
		toStrings.add(stringA2);
		toStrings.add(stringE2);

		trackManager.allocateNotesToStrings(fromStrings, beats, toStrings, MAX_FRET);

		TGVoice voice = beats.get(0).getVoice(0);
		assertEquals(2, voice.countNotes());
		assertTrue(beatContainsNote(beats.get(0), 1, 10));
		assertTrue(beatContainsNote(beats.get(0), 2, 12));

		// same test, but different order of notes in original chord
		beat = factory.newBeat();
		noteE3 = factory.newNote();
		noteE3.setString(2);
		noteE3.setValue(2);
		beat.getVoice(0).addNote(noteE3);
		noteG3 = factory.newNote();
		noteG3.setString(1);
		noteG3.setValue(0);
		beat.getVoice(0).addNote(noteG3);

		beats = new ArrayList<TGBeat>();
		beats.add(beat);

		trackManager.allocateNotesToStrings(fromStrings, beats, toStrings, MAX_FRET);

		voice = beats.get(0).getVoice(0);
		assertEquals(2, voice.countNotes());
		assertTrue(beatContainsNote(beats.get(0), 1, 10));
		assertTrue(beatContainsNote(beats.get(0), 2, 12));

	}

	@Test
	public void testDistributeNotes() {
		TGTrackManager trackManager = new TGTrackManager(new TGSongManager());

		List<Integer> fromStrings = new ArrayList<Integer>();
		fromStrings.add(0);

		// all notes of a major A chord on 5th fret, allocated to string with value zero
		TGBeat beat = factory.newBeat();
		TGNote noteA4 = factory.newNote();
		noteA4.setString(1);
		noteA4.setValue(69);
		beat.getVoice(0).addNote(noteA4);
		TGNote noteE4 = factory.newNote();
		noteE4.setString(1);
		noteE4.setValue(64);
		beat.getVoice(0).addNote(noteE4);
		TGNote noteC3sharp = factory.newNote();
		noteC3sharp.setString(1);
		noteC3sharp.setValue(61);
		beat.getVoice(0).addNote(noteC3sharp);
		TGNote noteA3 = factory.newNote();
		noteA3.setString(1);
		noteA3.setValue(57);
		beat.getVoice(0).addNote(noteA3);
		TGNote noteE3 = factory.newNote();
		noteE3.setString(1);
		noteE3.setValue(52);
		beat.getVoice(0).addNote(noteE3);
		TGNote noteA2 = factory.newNote();
		noteA2.setString(1);
		noteA2.setValue(45);
		beat.getVoice(0).addNote(noteA2);

		List<TGBeat> beats = new ArrayList<TGBeat>();
		beats.add(beat);

		stringE4.setNumber(1);
		stringB3.setNumber(2);
		stringG3.setNumber(3);
		stringD3.setNumber(4);
		stringA2.setNumber(5);
		stringE2.setNumber(6);
		List<TGString> toStrings = new ArrayList<TGString>();
		toStrings.add(stringE4);
		toStrings.add(stringB3);
		toStrings.add(stringG3);
		toStrings.add(stringD3);
		toStrings.add(stringA2);
		toStrings.add(stringE2);

		trackManager.allocateNotesToStrings(fromStrings, beats, toStrings, MAX_FRET);

		TGVoice voice = beats.get(0).getVoice(0);
		assertEquals(6, voice.countNotes());
		assertTrue(beatContainsNote(beats.get(0), 1, 5));
		assertTrue(beatContainsNote(beats.get(0), 2, 5));
		assertTrue(beatContainsNote(beats.get(0), 3, 6));
		assertTrue(beatContainsNote(beats.get(0), 4, 7));
		assertTrue(beatContainsNote(beats.get(0), 5, 7));
		assertTrue(beatContainsNote(beats.get(0), 6, 5));
	}

	@Test
	public void testNoTuningChange() {
		TGTrackManager trackManager = new TGTrackManager(new TGSongManager());

		List<Integer> fromStrings = new ArrayList<Integer>();
		fromStrings.add(stringG3.getValue());
		fromStrings.add(stringD3.getValue());

		TGBeat beat = factory.newBeat();
		TGNote note = factory.newNote();
		note.setString(2);
		note.setValue(16);
		beat.getVoice(0).addNote(note);

		List<TGBeat> beats = new ArrayList<TGBeat>();
		beats.add(beat);

		stringG3.setNumber(1);
		stringD3.setNumber(2);
		List<TGString> toStrings = new ArrayList<TGString>();
		toStrings.add(stringG3);
		toStrings.add(stringD3);

		trackManager.allocateNotesToStrings(fromStrings, beats, toStrings, MAX_FRET);

		TGVoice voice = beats.get(0).getVoice(0);
		assertEquals(1, voice.countNotes());
		assertTrue(beatContainsNote(beats.get(0), 2, 16));
	}

	@Test
	public void testToTrackWithLessStrings() {
		TGTrackManager trackManager = new TGTrackManager(new TGSongManager());

		List<Integer> fromStrings = new ArrayList<Integer>();
		fromStrings.add(stringG3.getValue());
		fromStrings.add(stringD3.getValue());

		TGBeat beat = factory.newBeat();
		TGNote noteG3 = factory.newNote();
		noteG3.setString(1);
		noteG3.setValue(0);
		beat.getVoice(0).addNote(noteG3);
		TGNote noteE3 = factory.newNote();
		noteE3.setString(2);
		noteE3.setValue(2);
		beat.getVoice(0).addNote(noteE3);

		List<TGBeat> beats = new ArrayList<TGBeat>();
		beats.add(beat);

		stringA2.setNumber(1);
		List<TGString> toStrings = new ArrayList<TGString>();
		toStrings.add(stringA2);

		trackManager.allocateNotesToStrings(fromStrings, beats, toStrings, MAX_FRET);

		TGVoice voice = beats.get(0).getVoice(0);
		assertEquals(1, voice.countNotes());
		assertTrue(beatContainsNote(beats.get(0), 1, 10) || beatContainsNote(beats.get(0), 1, 7));
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
		// the closest string of the second note is taken by the first
		assertEquals("s1f0 s2f23", retune("s1f0 s2f0", Arrays.asList(64, 63), MAX_FRET, 64, 40));
		// a note of voice 1 cannot take its string: voice 0 is allocated again from its original fret
		assertEquals("s1f1 | s2f11", retune("s1f0 | s1f5", STANDARD, MAX_FRET, 63, 58, 54, 49, 44, 39));
	}

	@Test
	public void withAnOpenStringOfPitchZeroNotesGoToTheLowestFretsAndThoseWithoutAFreeStringAreDropped() {
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

	private boolean beatContainsNote(TGBeat beat, int string, int value) {
		for (int i=0; i<beat.countVoices(); i++) {
			for (TGNote note : beat.getVoice(i).getNotes()) {
				if (string==note.getString() && value==note.getValue()) {
					return true;
				}
			}
		}
		return false;
	}
}
