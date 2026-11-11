package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins TGMeasureManager.fixVoiceOverlap: in one voice of a 4/4 measure, a voice that lasts past the start of the
 * next one is shortened to end where the next one starts (when that length can be written), and the beats are then
 * realigned: each one starts where the voices before it end.
 *
 * A voice is written "value@position": its duration value (4 = quarter) at its position in quarters.
 */
public class TestFixVoiceOverlap {

	private static final long QUARTER = MeasureFixture.QUARTER;

	private final MeasureFixture fixture = new MeasureFixture();

	private String fix(int voiceIndex) {
		return fix(fixture, voiceIndex);
	}

	private static String fix(MeasureFixture fixture, int voiceIndex) {
		fixture.manager.fixVoiceOverlap(fixture.measure, voiceIndex);
		return voices(fixture, voiceIndex);
	}

	// voice 0 of a new measure, written as the voices are printed, once fixed
	private static String fixed(String voices) {
		MeasureFixture fixture = new MeasureFixture();
		notes(fixture, 0, voices);
		return fix(fixture, 0);
	}

	// notes in a voice, written as the voices are printed: "value@position"
	private static void notes(MeasureFixture fixture, int voiceIndex, String voices) {
		for (String voice : voices.split(" ")) {
			String[] valueAtPosition = voice.split("@");
			long offset = Math.round(Double.parseDouble(valueAtPosition[1]) * QUARTER);
			fixture.note(offset, Integer.parseInt(valueAtPosition[0]), voiceIndex);
		}
	}

	private static String voices(MeasureFixture fixture, int voiceIndex) {
		List<String> voices = new ArrayList<String>();
		for (TGBeat beat : fixture.measure.getBeats()) {
			TGVoice voice = beat.getVoice(voiceIndex);
			if (!voice.isEmpty()) {
				double position = (beat.getPreciseStart() - fixture.measure.getPreciseStart()) / (double) QUARTER;
				voices.add(voice.getDuration().getValue() + "@" + position);
			}
		}
		return String.join(" ", voices);
	}

	@Test
	public void aVoiceOverlappingTheNextOneIsShortenedToEndWhereItStarts() {
		assertEquals("4@0.0 4@1.0 4@2.0 4@3.0", fixed("4@0 4@1 4@2 4@3"));
		assertEquals("4@0.0 4@1.0 2@2.0", fixed("2@0 4@1 2@2"));
		// then the later beats are realigned past the voices still sounding:
		// the quarter moves to where the second whole ends, past the measure end
		assertEquals("4@0.0 1@1.0 4@5.0", fixed("1@0 1@1 4@2"));
	}

	@Test
	public void anOverlapThatWouldLeaveALengthThatCannotBeWrittenIsKept() {
		fixture.note(0, TGDuration.QUARTER, 0);
		fixture.note(QUARTER - 1, TGDuration.QUARTER, 0);

		assertEquals("4@0.0 4@" + ((QUARTER - 1) / (double) QUARTER), fix(0));
	}

	@Test
	public void beatsWhereTheVoiceIsEmptyAreSkipped() {
		notes(fixture, 1, "8@0.5");
		notes(fixture, 0, "2@0 4@1");

		assertEquals("4@0.0 4@1.0", fix(0));
	}

	@Test
	public void onlyTheGivenVoiceIsFixed() {
		notes(fixture, 1, "2@0 4@1");
		notes(fixture, 0, "1@0 4@1");

		// the realignment still moves the beat shared with voice 0 past its whole
		assertEquals("4@0.0 4@4.0", fix(1));
		assertEquals("1@0.0 4@4.0", voices(fixture, 0));
	}
}
