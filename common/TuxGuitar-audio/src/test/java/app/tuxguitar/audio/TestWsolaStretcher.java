package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * The time-stretcher on synthetic signals, over the Percentage of Tempo range 1 to 500: a take of a measure at 120 bpm
 * (2 seconds at 44100 Hz) played at P% lasts 100 / P times as long. Pitch must stay within 1%, timing within 5 ms.
 */
public class TestWsolaStretcher {

	private static final int RATE = 44100;
	private static final int MEASURE = 88200;
	private static final int[] PERCENTS = {1, 25, 50, 80, 100, 125, 200, 400, 500};

	private final TGTimeStretcher stretcher = new TGWsolaStretcher(RATE);

	private static float[] sine(double frequency, double peak, int length) {
		float[] samples = new float[length];
		for (int i = 0; i < length; i++) {
			samples[i] = (float) (peak * Math.sin(2 * Math.PI * frequency * i / RATE));
		}
		return samples;
	}

	private static int stretched(int length, int percent) {
		return (int) Math.round(length * 100.0 / percent);
	}

	@Test
	public void atTheSameLengthTheSamplesAreUnchanged() {
		float[] input = sine(440, 0.4, 3000);
		input[1500] = 0.9f;
		float[] output = this.stretcher.stretch(input, 100, 2100, 2000);
		assertArrayEquals(Arrays.copyOfRange(input, 100, 2100), output);
	}

	@Test
	public void theOutputHasTheLengthAskedFor() {
		float[] input = sine(440, 0.4, MEASURE);
		for (int length : new int[] {1, 511, 1024, 17641, 220500, 70560}) {
			assertEquals(length, this.stretcher.stretch(input, 0, MEASURE, length).length);
		}
	}

	@Test
	public void aToneKeepsItsPitchAcrossThePercentageOfTempoRange() {
		float[] input = sine(440, 0.4, MEASURE);
		for (int percent : PERCENTS) {
			float[] output = this.stretcher.stretch(input, 0, MEASURE, stretched(MEASURE, percent));
			double frequency = Meters.frequency(output, 0, output.length, RATE);
			assertEquals(440, frequency, 4.4, "at " + percent + "%");
			assertEquals(0.4, Meters.peak(output, 0, output.length), 0.02, "peak at " + percent + "%");
		}
	}

	@Test
	public void lowAndHighTonesKeepTheirPitch() {
		for (double tone : new double[] {110, 220, 770, 2000}) {
			float[] input = sine(tone, 0.4, MEASURE);
			for (int percent : new int[] {50, 200}) {
				float[] output = this.stretcher.stretch(input, 0, MEASURE, stretched(MEASURE, percent));
				assertEquals(tone, Meters.frequency(output, 0, output.length, RATE), tone / 100, tone + " Hz at " + percent + "%");
			}
		}
	}

	@Test
	public void clicksStayOnTheirBeatsAndAreNotRepeated() {
		float[] input = new float[MEASURE];
		for (int beat = 0; beat < 4; beat++) {
			input[beat * 22050] = -0.25f;
		}
		for (int percent : PERCENTS) {
			int length = stretched(MEASURE, percent);
			float[] output = this.stretcher.stretch(input, 0, MEASURE, length);
			List<Integer> expected = Arrays.asList(0, length / 4, length / 2, 3 * length / 4);
			List<Integer> clicks = Meters.clicks(output, 0, length);
			assertEquals(4, clicks.size(), "at " + percent + "%: " + clicks);
			for (int beat = 0; beat < 4; beat++) {
				assertEquals(expected.get(beat), clicks.get(beat), 1, "click " + beat + " at " + percent + "%");
			}
		}
	}

	// an attack longer than one sample (here 300 samples) is played once too
	@Test
	public void attacksAreNotRepeated() {
		float[] input = new float[MEASURE];
		for (int beat = 0; beat < 4; beat++) {
			Arrays.fill(input, beat * 22050, beat * 22050 + 300, 0.5f);
		}
		for (int percent : PERCENTS) {
			int length = stretched(MEASURE, percent);
			List<Integer> bursts = Meters.clicks(this.stretcher.stretch(input, 0, MEASURE, length), 0, length);
			assertEquals(4, bursts.size(), "at " + percent + "%: " + bursts);
			for (int beat = 0; beat < 4; beat++) {
				assertEquals(beat * length / 4.0, bursts.get(beat), 1, "burst " + beat + " at " + percent + "%");
			}
		}
	}

	// clicks of -0.25 on a constant level of 0.3: each a sudden jump, so each an onset
	@Test
	public void clicksOnTopOfALevelStayOnTheirBeats() {
		float[] input = new float[MEASURE];
		Arrays.fill(input, 0.3f);
		for (int beat = 1; beat < 4; beat++) {
			input[beat * 22050] = 0.05f;
		}
		float[] output = this.stretcher.stretch(input, 0, MEASURE, 2 * MEASURE);
		for (int beat = 1; beat < 4; beat++) {
			assertEquals(0.05f, output[beat * 44100], 0.0001f, "beat " + beat);
			assertEquals(0.3f, output[beat * 44100 + 1], 0.0001f, "after beat " + beat);
		}
	}

	// loud noise has a sudden jump at nearly every sample: it is stretched without gaps all the same
	@Test
	public void noiseIsStretchedWithoutGaps() {
		java.util.Random random = new java.util.Random(7);
		float[] input = new float[MEASURE];
		for (int i = 0; i < MEASURE; i++) {
			input[i] = (float) (random.nextDouble() - 0.5);
		}
		for (int percent : new int[] {25, 200}) {
			int length = stretched(MEASURE, percent);
			float[] output = this.stretcher.stretch(input, 0, MEASURE, length);
			for (int at = 0; at + 100 <= length; at += 100) {
				assertTrue(Meters.peak(output, at, at + 100) > 0.05, "at " + percent + "%, samples " + at + " to " + (at + 100));
			}
		}
	}

	@Test
	public void aStretchTooShortForAFrameIsResampled() {
		float[] input = new float[1000];
		for (int i = 0; i < input.length; i++) {
			input[i] = i / 1000f;
		}
		float[] output = this.stretcher.stretch(input, 100, 400, 600);
		assertEquals(0.1f, output[0], 0.00001f);
		assertEquals(0.1005f, output[1], 0.00001f);
		assertEquals(0.399f, output[599], 0.00001f);
	}

	@Test
	public void aConstantLevelStaysConstant() {
		float[] input = new float[MEASURE];
		Arrays.fill(input, 0.4f);
		for (int percent : PERCENTS) {
			float[] output = this.stretcher.stretch(input, 0, MEASURE, stretched(MEASURE, percent));
			for (int i = 0; i < output.length; i++) {
				assertEquals(0.4f, output[i], 0.0001f, "sample " + i + " at " + percent + "%");
			}
		}
	}

	// a tone that gets louder half way, with no jump between two samples: where it gets louder moves with the
	// stretch. A frame read across the change carries it, so it can come early by up to a frame (23.2 ms) and the
	// search tolerance (4.6 ms) of the take's own time, times the stretch: unlike onsets and the ends of a stretch,
	// such changes are not within 5 ms when slowed down much
	@Test
	public void whatHappensInsideTheTakeStaysWithinAFrameOfWhereItShouldBe() {
		float[] input = sine(440, 0.1, 2 * MEASURE);
		int change = 44100;
		for (int i = MEASURE + change; i < 2 * MEASURE; i++) {
			input[i] *= 3;
		}
		for (int percent : PERCENTS) {
			int length = stretched(MEASURE, percent);
			float[] output = this.stretcher.stretch(input, MEASURE, 2 * MEASURE, length);
			int louder = 0;
			while (Math.abs(output[louder]) <= 0.2) {
				louder++;
			}
			long expected = Math.round(change * 100.0 / percent);
			double allowed = (1024 + 203 + RATE / 440) * Math.max(1, 100.0 / percent);
			assertTrue(Math.abs(louder - expected) <= allowed, "at " + percent + "%: " + louder + " for " + expected);
		}
	}

	@Test
	public void aStretchedStretchOfTheInputUsesOnlyThatStretch() {
		float[] input = new float[MEASURE];
		Arrays.fill(input, 0, 1000, 0.9f);
		Arrays.fill(input, 1000, 5000, 0.2f);
		Arrays.fill(input, 5000, MEASURE, 0.9f);
		float[] output = this.stretcher.stretch(input, 1000, 5000, 8000);
		assertEquals(0.2f, Meters.peak(output, 0, output.length), 0.0001f);
		assertEquals(0.2f, output[0], 0.0001f);
		assertEquals(0.2f, output[7999], 0.0001f);
	}
}
