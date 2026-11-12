package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;

/**
 * Pins TGMeasureManager.addSilence, the way the insert-rest action calls it: the voice of the beat becomes a rest
 * of the duration when the duration fits there (the rests after it are rewritten to fill up to the next note or
 * the end of the measure, and the following notes may be pushed into the trailing rests to make room). Without
 * room, the rest takes the duration the voice had, and that duration is copied into the given one. When even that
 * leaves a gap that cannot be written, the voice is left as it was, empty or not. The start position given to the
 * four argument form changes nothing that can be seen.
 *
 * The layout of voice 0 is MeasureFixture's, followed by " | " and the duration value of the voice at the
 * measure start.
 */
public class TestAddSilence {

	private static final long SIXTY_FOURTH = TGDuration.WHOLE_PRECISE_DURATION / 64;

	private final MeasureFixture fixture = new MeasureFixture();

	private TGDuration nonupletSixtyFourth() {
		TGDuration duration = fixture.duration(TGDuration.SIXTY_FOURTH);
		duration.getDivision().setEnters(9);
		duration.getDivision().setTimes(8);
		return duration;
	}

	private String layout() {
		return fixture.layout() + " | " + fixture.beatAt(0).getVoice(0).getDuration().getValue();
	}

	@Test
	public void anEmptyVoiceBecomesARestAndTheMeasureIsFilledWithRests() {
		fixture.manager.addSilence(fixture.beatAt(0), fixture.duration(TGDuration.QUARTER), 0);

		assertEquals("r@0 r@1 r@2 r@3 | 4", layout());
	}

	@Test
	public void aLongerRestIsShortenedAndTheMeasureIsFilledWithRests() {
		TGBeat beat = fixture.rest(0, TGDuration.WHOLE, 0).getBeat();

		fixture.manager.addSilence(beat, fixture.duration(TGDuration.EIGHTH), 0);

		assertEquals("r@0 r@0.5 r@1 r@1.5 r@2 r@2.5 r@3 r@3.5 | 8", layout());
	}

	@Test
	public void aLongerRestPushesTheFollowingNotesIntoTheTrailingRests() {
		TGBeat beat = fixture.beatAt(0);
		fixture.note(MeasureFixture.QUARTER, TGDuration.QUARTER, 0);
		fixture.rest(2 * MeasureFixture.QUARTER, TGDuration.HALF, 0);

		fixture.manager.addSilence(beat, fixture.duration(TGDuration.HALF), 0);

		// the trailing half rest is used up, and the quarter left after the note is not refilled
		assertEquals("r@0 n@2 | 2", layout());
	}

	@Test
	public void aRestWithoutRoomTakesTheDurationTheVoiceHad() {
		TGBeat beat = fixture.beatAt(0);
		fixture.quarterNotes(1, 2, 3);
		TGDuration half = fixture.duration(TGDuration.HALF);

		fixture.manager.addSilence(beat, half, 0);

		assertEquals("r@0 n@1 n@2 n@3 | 4 | 4", layout() + " | " + half.getValue());
	}

	@Test
	public void anEmptyVoiceStaysEmptyWhenTheRestDoesNotFit() {
		TGBeat beat = fixture.beatAt(0);
		fixture.note(SIXTY_FOURTH, TGDuration.SIXTY_FOURTH, 0);

		// leaves 8/9th of a 64th before the next note, which cannot be written
		fixture.manager.addSilence(beat, nonupletSixtyFourth(), 0);

		assertEquals("_@0 n@0.0625 | 4", layout());
	}

	@Test
	public void aVoiceIsKeptWhenTheRestDoesNotFit() {
		TGBeat beat = fixture.rest(0, TGDuration.QUARTER, 0).getBeat();
		fixture.note(SIXTY_FOURTH, TGDuration.SIXTY_FOURTH, 0);

		fixture.manager.addSilence(beat, nonupletSixtyFourth(), 0);

		assertEquals("r@0 n@0.0625 | 4", layout());
	}

	@Test
	public void theStartPositionChangesNothing() {
		long start = fixture.beatAt(0).getStart();

		fixture.manager.addSilence(fixture.beatAt(0), fixture.duration(TGDuration.QUARTER), start + 2 * TGDuration.QUARTER_TIME, 0);
		String insideTheMeasure = layout();
		fixture.manager.addSilence(fixture.beatAt(0), fixture.duration(TGDuration.QUARTER), start + 8 * TGDuration.QUARTER_TIME, 0);

		assertEquals("r@0 r@1 r@2 r@3 | 4 ; r@0 r@1 r@2 r@3 | 4", insideTheMeasure + " ; " + layout());
	}
}
