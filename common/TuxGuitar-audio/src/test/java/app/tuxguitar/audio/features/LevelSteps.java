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
		int[] bounds = heard().measure(measure);
		assertTone(samples, frequency, level, within, bounds[0], bounds[1]);
	}

	private static void assertTone(float[] samples, int frequency, float level, float within, int from, int to) {
		assertEquals(frequency, Meters.frequency(samples, from, to, TestInterface.SAMPLE_RATE), frequency / 100.0);
		assertEquals(level, Meters.peak(samples, from, to), within);
	}

	// over all that was heard (while stopped: all the input delivered)
	@Then("track {int} sounds a {int} Hz tone peaking at {float} within {float}")
	public void soundsATonePeakingAtThroughout(int number, int frequency, float level, float within) {
		float[] tap = tap(number, 0);
		assertTone(tap, frequency, level, within, 0, tap.length);
	}

	// nothing above -60 dBFS on either side of its tap
	@Then("track {int} sounds no tone above -60 dBFS")
	public void soundsNoTone(int number) {
		float[] left = tap(number, 0);
		float[] right = tap(number, 1);
		assertTrue(Math.max(Meters.peak(left, 0, left.length), Meters.peak(right, 0, right.length)) < SILENT);
	}

	@Then("track {int} sounds a {int} Hz tone peaking at {float} within {float} in measure {int}")
	public void soundsATonePeakingAt(int number, int frequency, float level, float within, int measure) {
		assertTone(tap(number, 0), frequency, level, within, measure);
	}

	@Then("track {int}'s {side} side sounds a {int} Hz tone peaking at {float} within {float} in measure {int}")
	public void sideSoundsATonePeakingAt(int number, int side, int frequency, float level, float within, int measure) {
		assertTone(tap(number, side), frequency, level, within, measure);
	}

	// the measures from the first's start to the last's end
	@Then("track {int} sounds a {int} Hz tone peaking at {float} within {float} and no {int} Hz tone above -60 dBFS in measures {int} to {int}")
	public void soundsAToneAndNoOtherInMeasures(int number, int frequency, float level, float within, int silent, int first, int last) {
		float[] tap = tap(number, 0);
		int from = heard().measure(first)[0];
		int to = heard().measure(last)[1];
		assertTone(tap, frequency, level, within, from, to);
		assertTrue(Meters.level(tap, from, to, silent, TestInterface.SAMPLE_RATE) < SILENT);
	}

	@Then("track {int} sounds a {int} Hz tone peaking at {float} within {float} on every pass")
	public void soundsATonePeakingOnEveryPass(int number, int frequency, float level, float within) {
		float[] tap = tap(number, 0);
		int passes = heard().timeline().get(heard().timeline().count() - 1).getPass() + 1;
		for (int pass = 0; pass < passes; pass++) {
			int end = (pass + 1 < passes ? heard().passStart(pass + 1) : (int) heard().timeline().length());
			assertEquals(frequency, Meters.frequency(tap, heard().passStart(pass), end, TestInterface.SAMPLE_RATE), frequency / 100.0, "pass " + (pass + 1));
			assertEquals(level, Meters.peak(tap, heard().passStart(pass), end), within, "pass " + (pass + 1));
		}
		assertTrue(passes > 1);
	}

	@Then("track {int} sounds a {int} Hz tone within 1 Hz in measure {int}")
	public void soundsAToneWithinOneHertz(int number, int frequency, int measure) {
		assertEquals(frequency, frequency(tap(number, 0), measure), 1.0);
	}

	// a tone among others in the tap is told by the frequency of its highest level near where it is looked for, over a
	// window: a short one where it starts (shorter than the first step of a bend, 22 ms at 120 bpm), long ones elsewhere
	private static final int SHORT = 960;
	private static final int LONG = 4096;
	private static final double NEAR = 25;

	// where the track's tap sounds in the measure: from its first sample above -60 dBFS to its last
	private int[] sounding(float[] tap, int measure) {
		int[] bounds = heard().measure(measure);
		int[] above = java.util.stream.IntStream.range(bounds[0], bounds[1]).filter(i -> Math.abs(tap[i]) >= SILENT).toArray();
		return new int[] {above[0], above[above.length - 1] + 1};
	}

	// starts on one frequency and ends, held, on the other
	@Then("track {int} sounds a {double} Hz tone rising to {double} Hz within 1 Hz in measure {int}")
	public void soundsAToneRisingTo(int number, double from, double to, int measure) {
		float[] tap = tap(number, 0);
		int[] sounding = sounding(tap, measure);
		double start = Meters.peakFrequency(tap, sounding[0], sounding[0] + SHORT, from - NEAR, to + NEAR, TestInterface.SAMPLE_RATE);
		double end = Meters.peakFrequency(tap, sounding[1] - LONG, sounding[1], from - NEAR, to + NEAR, TestInterface.SAMPLE_RATE);
		assertEquals(from, start, 1.0, "where it starts");
		assertEquals(to, end, 1.0, "where it ends");
	}

	// in every long window from where it sounds to where it stops
	@Then("track {int} sounds a {double} Hz tone that stays within 1 Hz in measure {int}")
	public void soundsAToneThatStays(int number, double frequency, int measure) {
		float[] tap = tap(number, 0);
		int[] sounding = sounding(tap, measure);
		assertTrue(sounding[1] - sounding[0] >= LONG, "sounds " + (sounding[1] - sounding[0]) + " samples");
		java.util.stream.IntStream.iterate(sounding[0], from -> from + LONG <= sounding[1], from -> from + LONG).forEach(from ->
			assertEquals(frequency, Meters.peakFrequency(tap, from, from + LONG, frequency - NEAR, frequency + NEAR, TestInterface.SAMPLE_RATE), 1.0, "from " + from));
	}

	// in the last long window before it stops sounding in the measure, a tone at each frequency
	@Then("track {int} ends measure {int} sounding a {double} Hz tone and a {double} Hz tone, each peaking above {float}")
	public void endsSoundingTwoTones(int number, int measure, double one, double other, float level) {
		float[] tap = tap(number, 0);
		int[] sounding = sounding(tap, measure);
		double first = Meters.level(tap, sounding[1] - LONG, sounding[1], one, TestInterface.SAMPLE_RATE);
		double second = Meters.level(tap, sounding[1] - LONG, sounding[1], other, TestInterface.SAMPLE_RATE);
		assertTrue(first > level && second > level, one + " Hz peaks at " + first + ", " + other + " Hz at " + second);
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
