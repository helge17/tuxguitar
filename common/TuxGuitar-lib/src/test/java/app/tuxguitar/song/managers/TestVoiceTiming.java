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
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how TGMeasureManager reads and repairs the timing of the voices of a 4/4 measure: realigning the beats
 * (updateBeatsPreciseStart), filling the gaps with rests (autoCompleteSilences) and listing the errors
 * (getMeasureErrors). A beat is "position:<voice 0>|<voice 1>", position in quarters from the measure start,
 * a voice "n<duration>" (notes), "r<duration>" (rest) or "_" (empty).
 */
public class TestVoiceTiming {

	private final MeasureFixture fixture = new MeasureFixture();
	private final TGMeasureManager manager = fixture.manager;

	private static String voice(TGVoice voice) {
		if (voice.isEmpty()) {
			return "_";
		}
		return (voice.isRestVoice() ? "r" : "n") + voice.getDuration().getValue();
	}

	private String beats() {
		List<TGBeat> beats = new ArrayList<TGBeat>(fixture.measure.getBeats());
		beats.sort(Comparator.comparingLong(TGBeat::getPreciseStart));
		List<String> described = new ArrayList<String>();
		for (TGBeat beat : beats) {
			double position = (beat.getPreciseStart() - fixture.measure.getPreciseStart()) / (double) QUARTER;
			String formatted = (position == Math.rint(position)) ? String.valueOf((long) position) : String.valueOf(position);
			described.add(formatted + ":" + voice(beat.getVoice(0)) + "|" + voice(beat.getVoice(1)));
		}
		return String.join(" ", described);
	}

	// --- realigning the beats ---

	@Test
	public void eachBeatMovesToWhereThePreviousOneEnds() {
		fixture.note(5, TGDuration.QUARTER, 0);
		fixture.note(QUARTER + 7, TGDuration.HALF, 0);
		fixture.note(3 * QUARTER + 5, TGDuration.QUARTER, 0);

		manager.updateBeatsPreciseStart(fixture.measure);

		assertEquals("0:n4|_ 1:n2|_ 3:n4|_", beats());
	}

	@Test
	public void aBeatClosesTheHoleBeforeItAndLeavesTheOverlap() {
		fixture.note(0, TGDuration.QUARTER, 0);
		fixture.note(2 * QUARTER, TGDuration.HALF, 0);
		fixture.note(3 * QUARTER, TGDuration.QUARTER, 0);

		manager.updateBeatsPreciseStart(fixture.measure);

		assertEquals("0:n4|_ 1:n2|_ 3:n4|_", beats());
	}

	@Test
	public void aBeatWaitsForTheLastOfItsVoicesToEnd() {
		fixture.note(0, TGDuration.QUARTER, 0);
		fixture.note(0, TGDuration.HALF, 1);
		fixture.note(QUARTER, TGDuration.QUARTER, 0);
		fixture.note(QUARTER, TGDuration.QUARTER, 1);

		manager.updateBeatsPreciseStart(fixture.measure);

		assertEquals("0:n4|n2 2:n4|n4", beats());
	}

	@Test
	public void aVoiceEmptyInTheFirstBeatStartsWhereTheShortestVoiceEnds() {
		fixture.note(0, TGDuration.HALF, 1);
		fixture.note(QUARTER, TGDuration.QUARTER, 0);

		manager.updateBeatsPreciseStart(fixture.measure);

		assertEquals("0:_|n2 2:n4|_", beats());
	}

	@Test
	public void aBeatWithoutVoicesAfterTheFirstIsRemoved() {
		fixture.note(0, TGDuration.QUARTER, 0);
		fixture.beatAt(QUARTER);
		fixture.note(2 * QUARTER, TGDuration.QUARTER, 0);

		manager.updateBeatsPreciseStart(fixture.measure);

		assertEquals("0:n4|_ 1:n4|_", beats());
	}

	@Test
	public void theFirstBeatIsAlignedEvenWithoutVoices() {
		fixture.beatAt(5);

		manager.updateBeatsPreciseStart(fixture.measure);

		assertEquals("0:_|_", beats());
	}

	// --- filling the gaps with rests ---

	@Test
	public void aMeasureWithoutBeatsIsFilledWithARestInTheFirstVoice() {
		manager.autoCompleteSilences(fixture.measure);

		assertEquals("0:r1|_", beats());
	}

	@Test
	public void aMeasureWithoutVoicesIsFilledWithARestInTheFirstVoice() {
		fixture.beatAt(QUARTER);

		manager.autoCompleteSilences(fixture.measure);

		assertEquals("0:r1|_ 1:_|_", beats());
	}

	@Test
	public void eachVoiceIsFilledBeforeItsFirstBeatBetweenItsBeatsAndUpToTheMeasureEnd() {
		fixture.note(QUARTER, TGDuration.QUARTER, 0);
		fixture.note(3 * QUARTER, TGDuration.QUARTER, 0);
		fixture.note(2 * QUARTER, TGDuration.QUARTER, 1);

		manager.autoCompleteSilences(fixture.measure);

		assertEquals("0:r4|r2 1:n4|_ 2:r4|n4 3:n4|r4", beats());
	}

	@Test
	public void aFullVoiceGetsNoRest() {
		fixture.quarterNotes(0, 1, 2, 3);

		manager.autoCompleteSilences(fixture.measure);

		assertEquals("0:n4|_ 1:n4|_ 2:n4|_ 3:n4|_", beats());
	}

	// --- listing the errors ---

	private String errors() {
		List<String> errors = new ArrayList<String>();
		for (TGMeasureError error : manager.getMeasureErrors(fixture.measure)) {
			if (error.getErrorType() == TGMeasureError.TYPE_TIED_NOTE_ERROR) {
				errors.add("tie s" + error.getInvalidTiedNote().getString() + "f" + error.getInvalidTiedNote().getValue());
			} else {
				errors.add("v" + error.getVoiceIndex() + ":" + error.getErrCode());
			}
		}
		return String.join(" ", errors) + (manager.isMeasureDurationValid(fixture.measure) ? " valid" : " invalid");
	}

	@Test
	public void aFullMeasureHasNoError() {
		fixture.quarterNotes(0, 1, 2, 3);

		assertEquals(" valid", errors());
	}

	@Test
	public void aVoiceWithAGapIsDiscontinuousAndOneWithAnOverlapOverlaps() {
		fixture.quarterNotes(0, 2, 3);
		fixture.note(0, TGDuration.HALF, 1);
		fixture.note(QUARTER, TGDuration.HALF, 1);
		fixture.note(3 * QUARTER, TGDuration.QUARTER, 1);

		assertEquals("v0:" + TGMeasureError.VOICE_DISCONTINUOUS + " v1:" + TGMeasureError.VOICE_OVERLAP + " invalid", errors());
	}

	@Test
	public void aVoiceEndingBeforeTheMeasureEndIsTooShortAndOneEndingAfterIsTooLong() {
		fixture.quarterNotes(0, 1, 2);
		fixture.note(0, TGDuration.HALF, 1);
		fixture.note(2 * QUARTER, TGDuration.WHOLE, 1);

		assertEquals("v0:" + TGMeasureError.VOICE_TOO_SHORT + " v1:" + TGMeasureError.VOICE_TOO_LONG + " invalid", errors());
	}

	@Test
	public void aVoiceStartingBeforeTheMeasureStartsEarlyAndErrorsAddUp() {
		fixture.note(-QUARTER, TGDuration.HALF, 0);
		fixture.note(QUARTER, TGDuration.HALF, 0);

		assertEquals("v0:" + (TGMeasureError.VOICE_EARLY_START | TGMeasureError.VOICE_TOO_SHORT) + " invalid", errors());
	}

	@Test
	public void aVoiceStartingAfterTheMeasureStartIsOnlyTooShort() {
		fixture.note(QUARTER, TGDuration.HALF, 0);

		assertEquals("v0:" + TGMeasureError.VOICE_TOO_SHORT + " invalid", errors());
	}

	@Test
	public void aTiedNoteWithoutAPreviousNoteOfItsFretIsAnErrorThatKeepsTheDurationsValid() {
		fixture.quarterNotes(0, 1, 2, 3);
		TGNote valid = fixture.beatAt(QUARTER).getVoice(0).getNote(0);
		valid.setTiedNote(true);
		TGNote invalid = fixture.beatAt(3 * QUARTER).getVoice(0).getNote(0);
		invalid.setValue(2);
		invalid.setTiedNote(true);

		assertEquals("tie s1f2 valid", errors());
	}
}
