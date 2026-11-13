package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.tuxguitar.audio.Meters;
import io.cucumber.java.en.Then;

/**
 * Steps on levels and sides: how loud a track's tap or the output peaks in a measure, the tone it sounds there, and
 * which side holds it.
 */
public class LevelSteps {

	// -60 dBFS
	private static final double SILENT = 0.001;

	private final AudioWorld world;

	public LevelSteps(AudioWorld world) {
		this.world = world;
	}

	private Heard heard() {
		return PlaySteps.heard(world);
	}

	// the side of the track's tap over the measure
	private float[] tap(int number, int side) {
		return heard().tap(world.track(number), side);
	}

	private float peak(float[] samples, int measure) {
		int[] bounds = heard().measure(measure);
		return Meters.peak(samples, bounds[0], bounds[1]);
	}

	private double frequency(float[] samples, int measure) {
		int[] bounds = heard().measure(measure);
		return Meters.frequency(samples, bounds[0], bounds[1], TestInterface.SAMPLE_RATE);
	}

	@Then("track {int} peaks at {float} within 1% in measure {int}")
	public void peaksAt(int number, float level, int measure) {
		float peak = Math.max(peak(tap(number, 0), measure), peak(tap(number, 1), measure));
		assertEquals(level, peak, level / 100);
	}

	private void assertTone(float[] samples, int frequency, float level, float within, int measure) {
		assertEquals(frequency, frequency(samples, measure), frequency / 100.0);
		assertEquals(level, peak(samples, measure), within);
	}

	@Then("track {int} sounds a {int} Hz tone peaking at {float} within {float} in measure {int}")
	public void soundsATonePeakingAt(int number, int frequency, float level, float within, int measure) {
		assertTone(tap(number, 0), frequency, level, within, measure);
	}

	@Then("track {int}'s {side} side sounds a {int} Hz tone peaking at {float} within {float} in measure {int}")
	public void sideSoundsATonePeakingAt(int number, int side, int frequency, float level, float within, int measure) {
		assertTone(tap(number, side), frequency, level, within, measure);
	}

	@Then("track {int} sounds a {int} Hz tone within 1 Hz in measure {int}")
	public void soundsAToneWithinOneHertz(int number, int frequency, int measure) {
		assertEquals(frequency, frequency(tap(number, 0), measure), 1.0);
	}

	@Then("the output peaks below {float} in measure {int}")
	public void theOutputPeaksBelow(float level, int measure) {
		float peak = Math.max(peak(heard().left(), measure), peak(heard().right(), measure));
		assertTrue(peak > SILENT && peak < level, "peak " + peak);
	}

	@Then("the output's {side} side peaks at {float} within {float} in measure {int}")
	public void theOutputsSidePeaksAt(int side, float level, float within, int measure) {
		assertEquals(level, peak(heard().output(side), measure), within);
	}

	@Then("the output's left and right sides peak within 2% of each other in measure {int}")
	public void theSidesPeakTogether(int measure) {
		float left = peak(heard().left(), measure);
		float right = peak(heard().right(), measure);
		assertTrue(left > SILENT, "left " + left);
		assertEquals(left, right, left / 50);
	}

	private double level(int side, int frequency) {
		float[] samples = heard().output(side);
		return Meters.level(samples, 0, samples.length, frequency, TestInterface.SAMPLE_RATE);
	}

	@Then("the output's {side} side holds a {int} Hz tone above -60 dBFS")
	public void theOutputsSideHoldsATone(int side, int frequency) {
		assertTrue(level(side, frequency) >= SILENT, "level " + level(side, frequency));
	}

	@Then("the output's {side} side holds no {int} Hz tone above -60 dBFS")
	public void theOutputsSideHoldsNoTone(int side, int frequency) {
		assertTrue(level(side, frequency) < SILENT, "level " + level(side, frequency));
	}
}
