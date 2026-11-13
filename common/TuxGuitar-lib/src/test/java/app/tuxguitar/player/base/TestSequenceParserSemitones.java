package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;

/**
 * The exact semitones each pitch bend of MidiSequenceParser carries next to its 7-bit value (which TestSequenceParserNotes
 * pins), for the instruments that bend a note by semitones (a VST3 instrument). A bend is written "TICK sVOICE
 * SEMITONES" (see ParserFixture.semitones); the quarter note is on string 1 from tick 960 to 1920.
 */
public class TestSequenceParserSemitones {

	private final ParserFixture song = new ParserFixture();

	// the bend's points, given as position, value pairs
	private List<String> bent(int... positionsAndValues) {
		TGEffectBend bend = song.factory.newEffectBend();
		for (int i = 0; i < positionsAndValues.length; i += 2) {
			bend.addPoint(positionsAndValues[i], positionsAndValues[i + 1]);
		}
		song.quarter(1, 0).getEffect().setBend(bend);
		return song.semitones();
	}

	// a bend point's value counts quarter tones: 4, a full bend, is a whole tone (its 7-bit value, 75, is 2.06 semitones
	// in the pitch bend range of 12)
	@Test
	public void aBendCarriesItsExactSemitones() {
		assertEquals(Arrays.asList("960 s-1 0.0", "1200 s1 2.0", "1920 s1 0.0"), bent(3, 4));
	}

	// each step towards the next point carries its share of the semitones between them
	@Test
	public void theStepsOfABendCarryTheirShareOfItsSemitones() {
		assertEquals(Arrays.asList("960 s-1 0.0", "960 s1 0.0", "1056 s1 0.2", "1152 s1 0.4", "1248 s1 0.6", "1344 s1 0.8",
				"1440 s1 1.0", "1440 s1 1.0", "1920 s1 0.0"), bent(0, 0, 6, 2));
	}

	// a tremolo bar point's value counts semitones, beyond the 7-bit range too (which plays 0, its lowest)
	@Test
	public void aTremoloBarCarriesItsExactSemitonesBeyondThePitchRange() {
		TGEffectTremoloBar tremoloBar = song.factory.newEffectTremoloBar();
		tremoloBar.addPoint(0, -12);
		tremoloBar.addPoint(12, -12);
		song.quarter(1, 0).getEffect().setTremoloBar(tremoloBar);

		assertEquals(Arrays.asList("960 s-1 0.0", "960 s1 -12.0", "1440 s1 -12.0", "1920 s1 -12.0", "1920 s1 -12.0",
				"1920 s1 0.0"), song.semitones());
	}

	// a vibrato swings a quarter of a semitone
	@Test
	public void aVibratoCarriesItsExactSemitones() {
		song.quarter(1, 0).getEffect().setVibrato(true);

		assertEquals(Arrays.asList("960 s-1 0.0", "1120 s1 0.0", "1280 s1 0.25", "1440 s1 0.0", "1600 s1 0.25", "1760 s1 0.0",
				"1920 s1 0.25", "1920 s1 0.0"), song.semitones());
	}

	// a slide two frets up, to the next note on the string, glides two semitones
	@Test
	public void aSlideCarriesItsExactSemitones() {
		song.quarter(1, 0).getEffect().setSlide(true);
		song.note(4, TGDuration.QUARTER, 1, 2);

		assertEquals(Arrays.asList("960 s-1 0.0", "1080 s1 0.25", "1200 s1 0.5", "1320 s1 0.75", "1440 s1 1.0", "1560 s1 1.25",
				"1680 s1 1.5", "1800 s1 1.75", "1920 s1 2.0", "1920 s1 0.0"), song.semitones());
	}
}
