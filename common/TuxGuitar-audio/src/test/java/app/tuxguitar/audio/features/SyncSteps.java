package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.audio.Meters;
import app.tuxguitar.audio.TGTimeline;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;

/**
 * Steps on keeping audio in sync: the clicks of track 1 (left) and track 2 (right) where they reach the output, and
 * against the beats. The render holds what the output sounds, plugin delay compensation taken out: live the output
 * sounds it the render's latency later.
 */
public class SyncSteps {

	private final AudioWorld world;

	public SyncSteps(AudioWorld world) {
		this.world = world;
	}

	private Heard heard() {
		return PlaySteps.heard(world);
	}

	static List<Integer> clicks(Heard heard, int side) {
		float[] output = heard.output(side);
		return Meters.clicks(output, 0, output.length);
	}

	// the output, or the file exported
	@Then("the {sound}'s left side holds {int} clicks and its right side {int} clicks")
	public void theOutputHoldsClicks(Heard sound, int left, int right) {
		assertClicks(sound, left, right);
	}

	static void assertClicks(Heard sound, int left, int right) {
		assertEquals(left, clicks(sound, 0).size(), "left clicks at " + clicks(sound, 0));
		assertEquals(right, clicks(sound, 1).size(), "right clicks at " + clicks(sound, 1));
	}

	@Then("the output's left side holds {int} clicks and its right side none")
	public void theOutputHoldsLeftClicksOnly(int left) {
		assertClicks(heard(), left, 0);
	}

	static void assertTogether(Heard heard, int within) {
		List<Integer> left = clicks(heard, 0);
		List<Integer> right = clicks(heard, 1);
		assertFalse(left.isEmpty());
		assertEquals(left.size(), right.size(), "left " + left + ", right " + right);
		for (int i = 0; i < left.size(); i++) {
			assertEquals(left.get(i), right.get(i), within, "left " + left + ", right " + right);
		}
	}

	@Then("on every beat the left and right clicks reach the output within 1 sample of each other")
	public void togetherWithinASample() {
		assertTogether(heard(), 1);
	}

	@Then("on every beat the left and right clicks reach the output within 5 ms of each other")
	public void togetherWithinFiveMilliseconds() {
		assertTogether(heard(), TestInterface.SAMPLE_RATE / 200);
	}

	// track 1's click nearest to where the position sounds, in its tap (where it joins the mix, as the output has it)
	private int leftClickAt(SongPosition position) {
		float[] tap = heard().tap(world.track(1));
		int at = heard().sampleAt(position, 0);
		return Meters.clicks(tap, 0, tap.length).stream().min((a, b) -> Integer.compare(Math.abs(a - at), Math.abs(b - at))).get();
	}

	// a Count In click is where its note reaches the drums
	@Then("the last Count In click reaches the output {int} samples before the left click of {position}, within 1 sample")
	public void theLastCountInClickReachesTheOutputBefore(int samples, SongPosition position) {
		long[] clicks = heard().timeline().getCountIn().positions();
		assertFalse(clicks.length == 0);
		assertEquals(samples, (double) (leftClickAt(position) - clicks[clicks.length - 1]), 1.0);
	}

	// the measures' clicks, next to a tone in the measures before (README: a jump of more than 0.1)
	@Then("on every beat of measures {int} to {int} the left and right clicks reach the output within 5 ms of each other")
	public void togetherInMeasures(int first, int last) {
		int from = heard().measure(first)[0];
		int to = heard().measure(last)[1];
		List<Integer> left = Meters.jumps(heard().left(), from, to);
		List<Integer> right = Meters.jumps(heard().right(), from, to);
		assertFalse(left.isEmpty());
		assertEquals(left.size(), right.size(), "left " + left + ", right " + right);
		for (int i = 0; i < left.size(); i++) {
			assertEquals(left.get(i), right.get(i), TestInterface.SAMPLE_RATE / 200, "left " + left + ", right " + right);
		}
	}

	// the beats' clicks, each followed by its echo 0.3 s later (Test Echo), and nothing else
	@Then("the output's right side holds {int} clicks: the {int} of measure {int} and their echoes")
	public void theRightSideHoldsClicksAndEchoes(int count, int clicks, int measure) {
		List<Integer> right = Meters.jumps(heard().right(), 0, heard().right().length);
		assertEquals(count, right.size(), "right clicks at " + right);
		int[] bounds = heard().measure(measure);
		List<Integer> beats = heard().beats().stream().filter(beat -> beat >= bounds[0] && beat < bounds[1]).collect(Collectors.toList());
		assertEquals(clicks, beats.size());
		int echo = (int) Math.round(0.3 * TestInterface.SAMPLE_RATE);
		for (int beat : beats) {
			assertTrue(right.stream().anyMatch(click -> Math.abs(click - beat) <= 1), "no click on " + beat + ": " + right);
			assertTrue(right.stream().anyMatch(click -> Math.abs(click - beat - echo) <= 1), "no echo of " + beat + ": " + right);
		}
	}

	// an echo is a click on the right side (track 2's), so many samples into the second pass: after the first jump back
	// (of a loop or a repeat)
	// "the echo of the click ..." or "the echo of the first pass's click ..."
	@Then("the echo of the (first pass's )click of measure {int} beat {double} reaches the output {int} samples into the second pass, within 1 sample")
	public void theEchoReachesTheSecondPass(int measure, double beat, int samples) {
		assertClickAt(secondPass() + samples);
	}

	private int secondPass() {
		TGTimeline timeline = heard().timeline();
		for (int i = 0; i < timeline.count(); i++) {
			if (timeline.get(i).isJump()) {
				return (int) timeline.start(i);
			}
		}
		throw new IllegalStateException("no jump back");
	}

	private void assertClickAt(int sample) {
		assertClickAt(heard(), sample);
	}

	// a click (or an echo, next to the clicks) on the right side
	static void assertClickAt(Heard heard, int sample) {
		List<Integer> right = Meters.jumps(heard.right(), 0, heard.right().length);
		assertTrue(right.stream().anyMatch(click -> Math.abs(click - sample) <= 1), "no click at " + sample + ": " + right);
	}

	@ParameterType("1 sample|5 ms")
	public Integer tolerance(String tolerance) {
		return (tolerance.equals("1 sample") ? 1 : TestInterface.SAMPLE_RATE / 200);
	}

	// the tone before the left click of the beat, and the other from it on, each over 2000 samples clear of the
	// tolerance
	@Then("track {int} sounds {int} Hz, then {int} Hz from the left click of {position} on, within {tolerance}")
	public void soundsThenFromTheLeftClick(int number, int before, int after, SongPosition position, int tolerance) {
		float[] tap = heard().tap(world.track(number));
		int click = leftClickAt(position);
		assertEquals(before, Meters.frequency(tap, click - tolerance - 2000, click - tolerance, TestInterface.SAMPLE_RATE), before / 100.0);
		assertEquals(after, Meters.frequency(tap, click + tolerance, click + tolerance + 2000, TestInterface.SAMPLE_RATE), after / 100.0);
	}

	@Then("the passes last {double}, {double} and {double} seconds within 5 ms")
	public void thePassesLast(double first, double second, double third) {
		double[] expected = {first, second, third};
		for (int pass = 0; pass < 3; pass++) {
			int end = (pass < 2 ? heard().passStart(pass + 1) : (int) heard().timeline().length());
			assertEquals(expected[pass], (end - heard().passStart(pass)) / (double) TestInterface.SAMPLE_RATE, 0.005, "pass " + (pass + 1));
		}
	}

	@Then("track {int} sounds a {int} Hz tone within 1% on every pass")
	public void soundsAToneOnEveryPass(int number, int frequency) {
		float[] tap = heard().tap(world.track(number));
		int passes = heard().timeline().get(heard().timeline().count() - 1).getPass() + 1;
		for (int pass = 0; pass < passes; pass++) {
			int end = (pass + 1 < passes ? heard().passStart(pass + 1) : (int) heard().timeline().length());
			assertEquals(frequency, Meters.frequency(tap, heard().passStart(pass), end, TestInterface.SAMPLE_RATE), frequency / 100.0, "pass " + (pass + 1));
		}
		assertTrue(passes > 1);
	}

	// the player played the click when it heard its position (the output's chain delay after the clock); the output
	// sounds the render its latency after the clock, plus the output latency, which both sides have
	@Then("the click reaches the output {int} samples after the player played it, within 1 sample")
	public void theClickReachesTheOutputLate(int late) {
		List<Integer> clicks = Meters.clicks(heard().left(), 0, heard().left().length);
		assertEquals(1, clicks.size(), "clicks at " + clicks);
		int played = heard().sampleAt(world.playedClick(), 0) + world.device().getChainDelay();
		assertEquals(late, clicks.get(0) + heard().latency() - played, 1);
	}

	// live, the output sounds the render its latency later than the clock
	@Then("every {side} click reaches the output {int} samples after its beat, within 1 sample")
	public void everyClickReachesTheOutputLate(int side, int late) {
		List<Integer> clicks = clicks(heard(), side);
		List<Integer> beats = heard().beats();
		assertEquals(beats.size(), clicks.size(), "clicks at " + clicks);
		for (int i = 0; i < clicks.size(); i++) {
			assertEquals(beats.get(i) + late, clicks.get(i) + heard().latency(), 1, "click " + i);
		}
	}
}
