package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.helpers.TGMeasureError;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how TGMeasureManager reads and repairs the timing of the voices of a 4/4 measure: realigning the beats
 * (updateBeatsPreciseStart), filling the gaps with rests (autoCompleteSilences) and listing the errors
 * (getMeasureErrors). Notes are given as (precise offset, duration value, voice) triples. A beat is
 * "position:<voice 0>|<voice 1>", position in quarters from the measure start, a voice "n<duration>" (notes),
 * "r<duration>" (rest) or "_" (empty).
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

	// adds a note for each (precise offset from the measure start, duration value, voice) triple
	private void notes(long... triples) {
		for (int i = 0; i < triples.length; i += 3) {
			fixture.note(triples[i], (int) triples[i + 1], (int) triples[i + 2]);
		}
	}

	// --- realigning the beats ---

	private String realigned(long... triples) {
		notes(triples);
		manager.updateBeatsPreciseStart(fixture.measure);
		return beats();
	}

	@Test
	public void eachBeatMovesToWhereThePreviousOneEnds() {
		assertEquals("0:n4|_ 1:n2|_ 3:n4|_", realigned(5, 4, 0, QUARTER + 7, 2, 0, 3 * QUARTER + 5, 4, 0));
	}

	@Test
	public void aBeatClosesTheHoleBeforeItAndLeavesTheOverlap() {
		assertEquals("0:n4|_ 1:n2|_ 3:n4|_", realigned(0, 4, 0, 2 * QUARTER, 2, 0, 3 * QUARTER, 4, 0));
	}

	@Test
	public void aBeatWaitsForTheLastOfItsVoicesToEnd() {
		assertEquals("0:n4|n2 2:n4|n4", realigned(0, 4, 0, 0, 2, 1, QUARTER, 4, 0, QUARTER, 4, 1));
	}

	@Test
	public void aVoiceEmptyInTheFirstBeatStartsWhereTheShortestVoiceEnds() {
		assertEquals("0:_|n2 2:n4|_", realigned(0, 2, 1, QUARTER, 4, 0));
	}

	@Test
	public void aBeatWithoutVoicesAfterTheFirstIsRemoved() {
		fixture.beatAt(QUARTER);

		assertEquals("0:n4|_ 1:n4|_", realigned(0, 4, 0, 2 * QUARTER, 4, 0));
	}

	@Test
	public void theFirstBeatIsAlignedEvenWithoutVoices() {
		fixture.beatAt(5);

		assertEquals("0:_|_", realigned());
	}

	// --- filling the gaps with rests ---

	private String completed(long... triples) {
		notes(triples);
		manager.autoCompleteSilences(fixture.measure);
		return beats();
	}

	@Test
	public void aMeasureWithoutBeatsIsFilledWithARestInTheFirstVoice() {
		assertEquals("0:r1|_", completed());
	}

	@Test
	public void aMeasureWithoutVoicesIsFilledWithARestInTheFirstVoice() {
		fixture.beatAt(QUARTER);

		assertEquals("0:r1|_ 1:_|_", completed());
	}

	@Test
	public void eachVoiceIsFilledBeforeItsFirstBeatBetweenItsBeatsAndUpToTheMeasureEnd() {
		assertEquals("0:r4|r2 1:n4|_ 2:r4|n4 3:n4|r4", completed(QUARTER, 4, 0, 3 * QUARTER, 4, 0, 2 * QUARTER, 4, 1));
	}

	@Test
	public void aFullVoiceGetsNoRest() {
		assertEquals("0:n4|_ 1:n4|_ 2:n4|_ 3:n4|_", completed(0, 4, 0, QUARTER, 4, 0, 2 * QUARTER, 4, 0, 3 * QUARTER, 4, 0));
	}

	// --- listing the errors ---

	private String errors(long... triples) {
		notes(triples);
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
		assertEquals(" valid", errors(0, 4, 0, QUARTER, 4, 0, 2 * QUARTER, 4, 0, 3 * QUARTER, 4, 0));
	}

	@Test
	public void aVoiceWithAGapIsDiscontinuousAndOneWithAnOverlapOverlaps() {
		assertEquals("v0:" + TGMeasureError.VOICE_DISCONTINUOUS + " v1:" + TGMeasureError.VOICE_OVERLAP + " invalid",
			errors(0, 4, 0, 2 * QUARTER, 4, 0, 3 * QUARTER, 4, 0, 0, 2, 1, QUARTER, 2, 1, 3 * QUARTER, 4, 1));
	}

	@Test
	public void aVoiceEndingBeforeTheMeasureEndIsTooShortAndOneEndingAfterIsTooLong() {
		assertEquals("v0:" + TGMeasureError.VOICE_TOO_SHORT + " v1:" + TGMeasureError.VOICE_TOO_LONG + " invalid",
			errors(0, 4, 0, QUARTER, 4, 0, 2 * QUARTER, 4, 0, 0, 2, 1, 2 * QUARTER, 1, 1));
	}

	@Test
	public void aVoiceStartingBeforeTheMeasureStartsEarlyAndErrorsAddUp() {
		assertEquals("v0:" + (TGMeasureError.VOICE_EARLY_START | TGMeasureError.VOICE_TOO_SHORT) + " invalid", errors(-QUARTER, 2, 0, QUARTER, 2, 0));
	}

	@Test
	public void aVoiceStartingAfterTheMeasureStartIsOnlyTooShort() {
		assertEquals("v0:" + TGMeasureError.VOICE_TOO_SHORT + " invalid", errors(QUARTER, 2, 0));
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
