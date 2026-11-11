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
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectGrace;

/**
 * Pins how the notes of a drum track are laid on its strings (TGTrackManager.allocatePercussionNotesToStrings, used
 * by the MIDI import): each note becomes the pitch it played on its former string, and takes the string the drum
 * map prefers for that pitch, else the closest free one (the higher string of two equally close), else is removed.
 * A string holds one note per beat, across its voices.
 *
 * The drum strings 1..n are tuned to 0, so a fret is the pitch. Preferred strings: crash 49 -> 1, closed hi-hat 42
 * and ride 51 -> 2, hi-mid tom 48 -> 3, snare 38 -> 4, low tom 45 -> 5, bass drum 36 -> 6.
 * A voice is described as its "s<string>f<fret>" notes, in note order.
 */
public class TestAllocatePercussionNotes {

	private static final int CRASH = 49, HIHAT = 42, RIDE = 51, SNARE = 38, BASS_DRUM = 36;

	private final TGFactory factory = new TGFactory();
	private final TGTrackManager trackManager = new TGTrackManager(new TGSongManager());
	private final List<TGBeat> beats = new ArrayList<TGBeat>();

	private TGBeat beat() {
		TGBeat beat = factory.newBeat();
		beats.add(beat);
		return beat;
	}

	private TGNote note(TGBeat beat, int voice, int string, int fret) {
		TGNote note = factory.newNote();
		note.setString(string);
		note.setValue(fret);
		beat.getVoice(voice).addNote(note);
		return note;
	}

	private List<TGString> drumStrings(int count) {
		List<TGString> strings = new ArrayList<TGString>();
		for (int number = 1; number <= count; number++) {
			TGString string = factory.newString();
			string.setNumber(number);
			string.setValue(0);
			strings.add(string);
		}
		return strings;
	}

	private void allocate(List<Integer> fromStringValues, int stringCount) {
		trackManager.allocatePercussionNotesToStrings(fromStringValues, beats, drumStrings(stringCount));
	}

	private void allocate(int stringCount) {
		allocate(Arrays.asList(0, 0, 0, 0, 0, 0), stringCount);
	}

	private static String describe(TGVoice voice) {
		StringBuilder description = new StringBuilder();
		for (TGNote note : voice.getNotes()) {
			description.append(description.length() == 0 ? "" : " ").append("s" + note.getString() + "f" + note.getValue());
		}
		return description.toString();
	}

	@Test
	public void eachDrumTakesItsPreferredString() {
		TGBeat beat = beat();
		note(beat, 0, 1, BASS_DRUM);
		note(beat, 0, 2, SNARE);
		note(beat, 0, 3, CRASH);

		allocate(6);

		assertEquals("s1f49 s4f38 s6f36", describe(beat.getVoice(0)));
	}

	@Test
	public void aTakenStringSendsTheDrumToTheClosestFreeOneTheHigherOnATie() {
		TGBeat beat = beat();
		note(beat, 0, 1, HIHAT);
		note(beat, 0, 2, RIDE);

		allocate(6);

		assertEquals("s2f42 s3f51", describe(beat.getVoice(0)));
	}

	@Test
	public void aDrumTakesALowerStringWhenNoneHigherIsFree() {
		TGBeat beat = beat();
		note(beat, 0, 1, HIHAT);
		note(beat, 0, 2, RIDE);

		allocate(2);

		assertEquals("s2f42 s1f51", describe(beat.getVoice(0)));
	}

	@Test
	public void aDrumWithoutAFreeStringIsRemoved() {
		TGBeat beat = beat();
		note(beat, 0, 1, BASS_DRUM);
		note(beat, 0, 2, CRASH);
		note(beat, 0, 3, SNARE);

		allocate(2);

		assertEquals("s1f49 s2f38", describe(beat.getVoice(0)));
	}

	@Test
	public void theVoicesOfABeatShareItsStrings() {
		TGBeat beat = beat();
		note(beat, 0, 1, HIHAT);
		note(beat, 1, 1, HIHAT);

		allocate(6);

		assertEquals("s2f42", describe(beat.getVoice(0)));
		assertEquals("s3f42", describe(beat.getVoice(1)));
	}

	@Test
	public void eachBeatStartsWithEveryStringFree() {
		TGBeat first = beat();
		note(first, 0, 1, HIHAT);
		TGBeat second = beat();
		note(second, 0, 1, HIHAT);

		allocate(6);

		assertEquals("s2f42", describe(first.getVoice(0)));
		assertEquals("s2f42", describe(second.getVoice(0)));
	}

	@Test
	public void aNoteBecomesThePitchItPlayedOnItsFormerString() {
		TGBeat beat = beat();
		note(beat, 0, 2, BASS_DRUM - 10);
		TGNote graced = note(beat, 0, 1, SNARE - 20);
		TGEffectGrace grace = factory.newEffectGrace();
		grace.setFret(3);
		graced.getEffect().setGrace(grace);

		allocate(Arrays.asList(20, 10), 6);

		assertEquals("s6f36 s4f38", describe(beat.getVoice(0)));
		assertEquals(23, graced.getEffect().getGrace().getFret());
	}
}
