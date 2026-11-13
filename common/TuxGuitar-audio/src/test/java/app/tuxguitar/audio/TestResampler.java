package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * A take recorded at one sample rate played on a timeline of another: resampled, it keeps its pitch, its level and
 * where its samples fall in time.
 */
public class TestResampler {

	private static float[][] sine(double frequency, int sampleRate, int length) {
		float[][] sine = new float[1][length];
		for (int i = 0; i < length; i++) {
			sine[0][i] = (float) (0.5 * Math.sin(2 * Math.PI * frequency * i / sampleRate));
		}
		return sine;
	}

	// the largest difference from the expected samples, away from the edges (where the input runs out)
	private static double error(float[] expected, float[] actual) {
		double error = 0;
		for (int i = 200; i < expected.length - 200; i++) {
			error = Math.max(error, Math.abs(expected[i] - actual[i]));
		}
		return error;
	}

	@Test
	public void atTheSameRateTheInputIsKept() {
		float[][] input = sine(440, 44100, 100);
		assertSame(input, TGResampler.resample(input, 44100, 44100));
	}

	// one second at either rate is one second at the other; a 1 kHz sine is the same sine at the new rate
	@Test
	public void aSineResampledIsTheSameSineAtTheNewRate() {
		for (int[] rates : new int[][] {{44100, 48000}, {48000, 44100}}) {
			float[] resampled = TGResampler.resample(sine(1000, rates[0], rates[0]), rates[0], rates[1])[0];
			assertEquals(rates[1], resampled.length);
			assertEquals(0, error(sine(1000, rates[1], rates[1])[0], resampled), 1e-3, rates[0] + " to " + rates[1]);
		}
	}

	// a click on a sample that falls on an output sample (every 147th at 44100 Hz falls on every 160th at 48000) keeps
	// that sample and its level; band-limited, it rings on the samples around, all below half of it: beats stay on
	// their samples
	@Test
	public void aClickOnASharedSampleKeepsItsSample() {
		float[][] click = new float[2][44100];
		click[1][22050] = -0.25f;
		float[][] resampled = TGResampler.resample(click, 44100, 48000);
		assertEquals(-0.25f, resampled[1][24000], 1e-6);
		resampled[1][24000] = 0;
		assertEquals("0.0", Float.toString(Meters.peak(resampled[0], 0, 48000)));
		double ringing = Meters.peak(resampled[1], 0, 48000);
		assertEquals(true, ringing > 0.01 && ringing < 0.125, "ringing " + ringing);
	}

	// a constant level passes unchanged, up to the edges
	@Test
	public void aConstantLevelIsKept() {
		float[][] level = new float[1][1000];
		java.util.Arrays.fill(level[0], 0.3f);
		float[] resampled = TGResampler.resample(level, 44100, 22050)[0];
		assertEquals(500, resampled.length);
		assertEquals(0.3f, resampled[250], 1e-6);
	}
}
