package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * The time-stretcher's settings, one at a time, at 44100 Hz: frames of 1024 samples, a frame every 512 (a hop), a
 * search 203 samples either side of where a frame should be read. An onset is a jump of more than 0.1 between two
 * samples, more than four times the step before it, a frame at least after the onset before it and a frame at least
 * before the end. The similarity of a candidate is its correlation with the natural continuation over a hop, every
 * second sample from the first, divided by the square root of the candidate's energy; the earliest of the best wins.
 */
public class TestWsolaKnobs {

	private final TGWsolaStretcher stretcher = new TGWsolaStretcher(44100);

	private static float[] clicksAt(int length, int... at) {
		float[] input = new float[length];
		for (int i : at) {
			input[i] = 1f;
		}
		return input;
	}

	@Test
	public void anOnsetIsAJumpOfMoreThanATenth() {
		float[] input = new float[8192];
		input[2000] = 0.1f;
		input[4000] = 0.11f;
		assertEquals(List.of(0, 4000), stretcher.onsets(input, 0, 8192));
	}

	// a step of 0.0625, then one of 0.25: four times it, not more
	@Test
	public void anOnsetIsMoreThanFourTimesTheStepBeforeIt() {
		float[] input = new float[8192];
		input[1999] = 0.0625f;
		input[2000] = 0.3125f;
		assertEquals(List.of(0), stretcher.onsets(input, 0, 8192));
	}

	@Test
	public void onsetsAreAFrameApartAtLeast() {
		assertEquals(List.of(0, 2000), stretcher.onsets(clicksAt(8192, 2000, 2500), 0, 8192));
		assertEquals(List.of(0, 2000, 3024), stretcher.onsets(clicksAt(8192, 2000, 3024), 0, 8192));
	}

	@Test
	public void anOnsetAFrameBeforeTheEndStillCounts() {
		assertEquals(List.of(0, 7168), stretcher.onsets(clicksAt(8192, 7168), 0, 8192));
	}

	// even samples at 0.5, odd ones at 0.25: compared on the even ones, a hop with itself scores the square root of
	// 256 * 0.25
	@Test
	public void aHopComparedWithItselfScoresTheSquareRootOfItsEnergy() {
		float[] input = new float[4096];
		for (int i = 0; i < input.length; i++) {
			input[i] = (i % 2 == 0 ? 0.5f : 0.25f);
		}
		assertEquals(8, stretcher.similarity(input, 1000, 1000, 4096), 1e-6);
	}

	@Test
	public void aLouderCopyScoresTheSame() {
		float[] input = new float[4096];
		for (int i = 0; i < 512; i++) {
			input[1000 + i] = 0.5f;
			input[2000 + i] = 1f;
		}
		assertEquals(8, stretcher.similarity(input, 1000, 2000, 4096), 1e-6);
	}

	// where the frame should be read: 5000, so the search runs from 4797 to 5203
	@Test
	public void amongEquallySimilarPositionsTheEarliestWins() {
		assertEquals(4797, stretcher.bestPosition(new float[20000], 0, 20000, 1000, 5000));
	}

	@Test
	public void theSearchReachesItsFarEnd() {
		float[] input = new float[20000];
		Random random = new Random(7);
		for (int i = 0; i < 512; i++) {
			input[1000 + i] = random.nextFloat() - 0.5f;
			input[5203 + i] = input[1000 + i];
		}
		assertEquals(5203, stretcher.bestPosition(input, 0, 20000, 1000, 5000));
	}

	// a piece of 1280 samples (shorter than a frame and a half) then a click: the frames of the piece read up to its
	// end, so the click sounds once, where the piece stretched to twice its length ends
	@Test
	public void aShortPieceDoesNotReadPastItsEnd() {
		float[] output = stretcher.stretch(new float[][] {clicksAt(4096, 1280)}, 0, 4096, 8192)[0];
		assertEquals(List.of(2560), Meters.clicks(output, 0, output.length));
	}

	// 500 samples of a ramp, from sample 2000 of the input, read between samples at half speed
	@Test
	public void aStretchShorterThanAFrameIsResampledWhereverItStarts() {
		float[] input = new float[4096];
		for (int i = 0; i < input.length; i++) {
			input[i] = i / 10000f;
		}
		float[] output = stretcher.stretch(new float[][] {input}, 2000, 2500, 1000)[0];
		assertEquals(0.2f, output[0], 1e-6);
		assertEquals(0.20005f, output[1], 1e-6);
		assertEquals(0.2499f, output[999], 1e-6);
	}

	// a 440 Hz tone on the left, noise on the right: both sides are read at the positions chosen on their mean, so
	// the two sides stretched add up to twice their mean stretched
	@Test
	public void bothSidesAreReadAtTheSamePositionsChosenOnTheirMean() {
		Random random = new Random(3);
		float[][] sides = new float[2][44100];
		float[] mean = new float[44100];
		for (int i = 0; i < mean.length; i++) {
			sides[0][i] = (float) (0.4 * Math.sin(2 * Math.PI * 440 * i / 44100));
			sides[1][i] = 0.2f * (random.nextFloat() - 0.5f);
			mean[i] = (sides[0][i] + sides[1][i]) / 2;
		}
		float[][] stereo = stretcher.stretch(sides, 0, 44100, 70000);
		float[] mono = stretcher.stretch(new float[][] {mean}, 0, 44100, 70000)[0];

		assertEquals(2, stereo.length);
		for (int i = 0; i < mono.length; i++) {
			assertEquals(2 * mono[i], stereo[0][i] + stereo[1][i], 1e-5, "sample " + i);
		}
	}

	// a stretch of exactly a frame is time-stretched, keeping its pitch, not resampled
	@Test
	public void aStretchOfAFrameKeepsItsPitch() {
		float[] input = new float[1024];
		for (int i = 0; i < input.length; i++) {
			input[i] = (float) (0.4 * Math.sin(2 * Math.PI * 2000 * i / 44100));
		}
		float[] output = stretcher.stretch(new float[][] {input}, 0, 1024, 2048)[0];
		assertEquals(2000, Meters.frequency(output, 0, output.length, 44100), 40);
	}
}
