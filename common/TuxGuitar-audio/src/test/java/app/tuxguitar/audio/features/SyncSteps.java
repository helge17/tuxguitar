package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import app.tuxguitar.audio.Meters;
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

	private List<Integer> clicks(int side) {
		float[] output = heard().output(side);
		return Meters.clicks(output, 0, output.length);
	}

	@Then("the output's left side holds {int} clicks and its right side {int} clicks")
	public void theOutputHoldsClicks(int left, int right) {
		assertEquals(left, clicks(0).size(), "left clicks at " + clicks(0));
		assertEquals(right, clicks(1).size(), "right clicks at " + clicks(1));
	}

	@Then("the output's left side holds {int} clicks and its right side none")
	public void theOutputHoldsLeftClicksOnly(int left) {
		theOutputHoldsClicks(left, 0);
	}

	private void assertTogether(int within) {
		List<Integer> left = clicks(0);
		List<Integer> right = clicks(1);
		assertFalse(left.isEmpty());
		assertEquals(left.size(), right.size(), "left " + left + ", right " + right);
		for (int i = 0; i < left.size(); i++) {
			assertEquals(left.get(i), right.get(i), within, "left " + left + ", right " + right);
		}
	}

	@Then("on every beat the left and right clicks reach the output within 1 sample of each other")
	public void togetherWithinASample() {
		assertTogether(1);
	}

	@Then("on every beat the left and right clicks reach the output within 5 ms of each other")
	public void togetherWithinFiveMilliseconds() {
		assertTogether(TestInterface.SAMPLE_RATE / 200);
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

	// live, the output sounds the render its latency later than the clock
	@Then("every left click reaches the output {int} samples after its beat, within 1 sample")
	public void everyLeftClickReachesTheOutputLate(int late) {
		List<Integer> left = clicks(0);
		List<Integer> beats = heard().beats();
		assertEquals(beats.size(), left.size(), "left clicks at " + left);
		for (int i = 0; i < left.size(); i++) {
			assertEquals(beats.get(i) + late, left.get(i) + heard().latency(), 1, "click " + i);
		}
	}
}
