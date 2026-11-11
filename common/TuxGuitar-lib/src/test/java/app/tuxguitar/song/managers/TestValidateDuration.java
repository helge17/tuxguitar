package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins TGMeasureManager.validateDuration: whether a duration fits at a beat of a 4/4 measure,
 * and how the measure is rearranged (rests absorbed, voices shifted) to make it fit.
 *
 * Layouts are written as "kind@position" for voice 0, position in quarters from the measure start:
 * n = note, r = rest, t = rest carrying a text, _ = beat whose voice 0 is empty.
 */
public class TestValidateDuration {

	private static final long QUARTER = TGDuration.WHOLE_PRECISE_DURATION / 4;
	private static final long SIXTY_FOURTH = TGDuration.WHOLE_PRECISE_DURATION / 64;

	private final TGSongManager songManager = new TGSongManager();
	private final TGFactory factory = songManager.getFactory();
	private final TGMeasureManager manager = songManager.getMeasureManager();
	private final TGMeasure measure = songManager.newSong().getTrack(0).getMeasure(0);

	// --- shortening an existing voice -------------------------------------------------------

	@Test
	public void shorteningIsAcceptedWhenTheRemainingGapCanBeWritten() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);

		assertTrue(validate(voice, duration(TGDuration.EIGHTH), false, false));
		assertEquals("n@0", layout());
	}

	@Test
	public void shorteningToTheSameLengthUpToTheNextNoteIsAccepted() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		note(QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(validate(voice, duration(TGDuration.QUARTER), false, false));
		assertEquals("n@0 n@1", layout());
	}

	@Test
	public void shorteningIsRefusedWhenTheGapBeforeTheNextNoteCannotBeWritten() {
		TGVoice voice = note(0, duration(TGDuration.SIXTY_FOURTH), 0);
		note(SIXTY_FOURTH, duration(TGDuration.SIXTY_FOURTH), 0);

		// leaves 1/9th of a 64th before the next note
		assertFalse(validate(voice, nonuplet(TGDuration.SIXTY_FOURTH), true, true));
		assertEquals("n@0 n@0.0625", layout());
	}

	@Test
	public void shorteningMeasuresTheGapPastRestsAndOtherVoices() {
		TGVoice voice = note(0, duration(TGDuration.SIXTY_FOURTH), 0);
		rest(SIXTY_FOURTH, duration(TGDuration.SIXTY_FOURTH), 0);
		note(2 * SIXTY_FOURTH, duration(TGDuration.SIXTY_FOURTH), 1);

		// the gap runs to the end of the measure, where it can be written
		assertTrue(validate(voice, nonuplet(TGDuration.SIXTY_FOURTH), false, false));
		assertEquals("n@0 r@0.0625 _@0.125", layout());
	}

	// --- a beat with nothing in the voice yet ------------------------------------------------

	@Test
	public void freeBeatFitsBeforeTheEndOfTheMeasure() {
		TGBeat beat = detachedBeat(3 * QUARTER);

		assertTrue(manager.validateDuration(measure, beat, 0, duration(TGDuration.QUARTER), false, false));
		assertEquals("", layout());
	}

	@Test
	public void freeBeatPastTheEndOfTheMeasureIsRefused() {
		TGBeat beat = detachedBeat(3 * QUARTER);

		assertFalse(manager.validateDuration(measure, beat, 0, duration(TGDuration.HALF), true, true));
		assertEquals("", layout());
	}

	@Test
	public void freeBeatFitsBeforeTheNextNote() {
		TGBeat beat = note(0, duration(TGDuration.HALF), 1).getBeat();
		note(2 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(manager.validateDuration(measure, beat, 0, duration(TGDuration.HALF), false, false));
		assertEquals("_@0 n@2", layout());
	}

	@Test
	public void freeBeatOverlappingTheNextNoteWithNoRestsToTakeIsRefused() {
		TGBeat beat = note(0, duration(TGDuration.HALF), 1).getBeat();
		note(2 * QUARTER, duration(TGDuration.QUARTER), 0);

		TGDuration whole = duration(TGDuration.WHOLE);
		assertFalse(manager.validateDuration(measure, beat, 0, whole, true, true));
		assertEquals(TGDuration.WHOLE, whole.getValue());
		assertEquals("_@0 n@2", layout());
	}

	@Test
	public void freeBeatTakesTheFollowingRests() {
		TGBeat beat = note(0, duration(TGDuration.QUARTER), 1).getBeat();
		rest(QUARTER, duration(TGDuration.QUARTER), 0);
		note(2 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(manager.validateDuration(measure, beat, 0, duration(TGDuration.HALF), false, false));
		assertEquals("_@0 n@2", layout());
	}

	// --- lengthening by taking the following rests ------------------------------------------

	@Test
	public void lengtheningTakesRestsUpToTheEndOfTheMeasure() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		rest(QUARTER, duration(TGDuration.QUARTER), 0);
		rest(2 * QUARTER, duration(TGDuration.QUARTER), 0);
		rest(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(validate(voice, duration(TGDuration.WHOLE), false, false));
		assertEquals("n@0", layout());
	}

	@Test
	public void lengtheningTakesRestsUpToTheNextNote() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		rest(QUARTER, duration(TGDuration.QUARTER), 0);
		note(2 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(validate(voice, duration(TGDuration.HALF), false, false));
		assertEquals("n@0 n@2", layout());
	}

	@Test
	public void lengtheningPastTheNextNoteIsRefusedAndLeavesTheRests() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		rest(QUARTER, duration(TGDuration.QUARTER), 0);
		note(2 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertFalse(validate(voice, duration(TGDuration.WHOLE), false, false));
		assertEquals("n@0 r@1 n@2", layout());
	}

	@Test
	public void restsCarryingTextStopTheRestsBeingTaken() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		rest(QUARTER, duration(TGDuration.QUARTER), 0);
		textRest(2 * QUARTER, duration(TGDuration.QUARTER), 0);
		rest(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertFalse(validate(voice, duration(TGDuration.WHOLE), false, false));
		assertEquals("n@0 r@1 t@2 r@3", layout());
		assertTrue(validate(voice, duration(TGDuration.HALF), false, false));
		assertEquals("n@0 t@2 r@3", layout());
	}

	@Test
	public void lengtheningUpToARestCarryingTextIsAccepted() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		textRest(2 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(validate(voice, duration(TGDuration.HALF), false, false));
		assertEquals("n@0 t@2", layout());
	}

	// --- lengthening by shifting the following voices into trailing rests -------------------

	@Test
	public void lengtheningShiftsTheFollowingNotesIntoTrailingRests() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		note(QUARTER, duration(TGDuration.QUARTER), 0);
		rest(2 * QUARTER, duration(TGDuration.QUARTER), 0);
		rest(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertFalse(validate(voice, duration(TGDuration.HALF), false, false));
		assertTrue(validate(voice, duration(TGDuration.HALF), true, false));
		assertEquals("n@0 n@2", layout());
	}

	@Test
	public void shiftingStopsOnceEnoughRoomIsMade() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		note(QUARTER, duration(TGDuration.QUARTER), 0);
		rest(2 * QUARTER, duration(TGDuration.QUARTER), 0);
		note(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(validate(voice, duration(TGDuration.HALF), true, false));
		assertEquals("n@0 n@2 n@3", layout());
	}

	@Test
	public void shiftingMakesRoomForTheSmallestPossibleOverlap() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		note(2 * QUARTER - 1, duration(TGDuration.QUARTER), 0);
		rest(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertTrue(validate(voice, duration(TGDuration.HALF), true, false));
		assertEquals("n@0 n@2", layout());
	}

	@Test
	public void shiftingIsRefusedWhenTheTrailingRestsAreTooShort() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		note(QUARTER, duration(TGDuration.QUARTER), 0);
		rest(2 * QUARTER, duration(TGDuration.QUARTER), 0);
		note(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		TGDuration dottedHalf = dotted(TGDuration.HALF);
		assertFalse(validate(voice, dottedHalf, true, false));
		assertEquals(TGDuration.HALF, dottedHalf.getValue());
		assertEquals("n@0 n@1 r@2 n@3", layout());
	}

	@Test
	public void lengtheningIntoAGapBeforeTheNextNoteNeedsShiftingAllowed() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		note(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		assertFalse(validate(voice, duration(TGDuration.HALF), false, false));
		assertTrue(validate(voice, duration(TGDuration.HALF), true, false));
		assertEquals("n@0 n@3", layout());
	}

	@Test
	public void lengtheningTheLastVoiceWithNoTrailingRestsIsRefused() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);

		assertFalse(validate(voice, duration(TGDuration.HALF), true, false));
		assertEquals("n@0", layout());
	}

	// --- last resort: keep the current duration ---------------------------------------------

	@Test
	public void whenNothingFitsTheCurrentDurationIsAdopted() {
		TGVoice voice = note(0, duration(TGDuration.QUARTER), 0);
		note(QUARTER, duration(TGDuration.QUARTER), 0);
		rest(2 * QUARTER, duration(TGDuration.QUARTER), 0);
		note(3 * QUARTER, duration(TGDuration.QUARTER), 0);

		TGDuration requested = dotted(TGDuration.HALF);
		assertTrue(validate(voice, requested, true, true));
		assertEquals(TGDuration.QUARTER, requested.getValue());
		assertFalse(requested.isDotted());
		assertEquals("n@0 n@1 r@2 n@3", layout());
	}

	// --- fixture ----------------------------------------------------------------------------

	private boolean validate(TGVoice voice, TGDuration duration, boolean moveNextBeats, boolean setCurrentDuration) {
		return manager.validateDuration(measure, voice.getBeat(), voice.getIndex(), duration, moveNextBeats, setCurrentDuration);
	}

	private TGDuration duration(int value) {
		TGDuration duration = factory.newDuration();
		duration.setValue(value);
		return duration;
	}

	private TGDuration dotted(int value) {
		TGDuration duration = duration(value);
		duration.setDotted(true);
		return duration;
	}

	private TGDuration nonuplet(int value) {
		TGDuration duration = duration(value);
		duration.getDivision().setEnters(9);
		duration.getDivision().setTimes(8);
		return duration;
	}

	private TGBeat detachedBeat(long offset) {
		TGBeat beat = factory.newBeat();
		beat.setPreciseStart(measure.getPreciseStart() + offset);
		beat.setMeasure(measure);
		return beat;
	}

	private TGBeat beatAt(long offset) {
		TGBeat beat = manager.getBeatPrecise(measure, measure.getPreciseStart() + offset);
		if (beat == null) {
			beat = detachedBeat(offset);
			manager.addBeat(measure, beat);
			manager.orderBeats(measure);
		}
		return beat;
	}

	private TGVoice rest(long offset, TGDuration duration, int voiceIndex) {
		TGVoice voice = beatAt(offset).getVoice(voiceIndex);
		voice.setEmpty(false);
		voice.getDuration().copyFrom(duration);
		return voice;
	}

	private TGVoice textRest(long offset, TGDuration duration, int voiceIndex) {
		TGVoice voice = rest(offset, duration, voiceIndex);
		TGText text = factory.newText();
		text.setValue("text");
		voice.getBeat().setText(text);
		return voice;
	}

	private TGVoice note(long offset, TGDuration duration, int voiceIndex) {
		TGVoice voice = rest(offset, duration, voiceIndex);
		TGNote note = factory.newNote();
		note.setString(1);
		voice.addNote(note);
		return voice;
	}

	private String layout() {
		StringBuilder layout = new StringBuilder();
		for (TGBeat beat : measure.getBeats()) {
			TGVoice voice = beat.getVoice(0);
			String kind = voice.isEmpty() ? "_" : (!voice.isRestVoice() ? "n" : (beat.isTextBeat() ? "t" : "r"));
			double position = (beat.getPreciseStart() - measure.getPreciseStart()) / (double) QUARTER;
			String formatted = (position == Math.rint(position)) ? String.valueOf((long) position) : String.valueOf(position);
			layout.append(layout.length() == 0 ? "" : " ").append(kind).append('@').append(formatted);
		}
		return layout.toString();
	}
}
