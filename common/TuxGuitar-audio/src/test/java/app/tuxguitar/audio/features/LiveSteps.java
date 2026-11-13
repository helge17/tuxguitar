package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import app.tuxguitar.audio.LiveOutput;
import app.tuxguitar.audio.Meters;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import io.cucumber.java.en.Then;

/**
 * Steps on what the TuxGuitar Synthesizer sounds live, outside an offline render: its output as the audio device plays
 * it in real time from the step on (see LiveOutput), while the threads TuxGuitar started run.
 */
public class LiveSteps {

	// -60 dBFS
	private static final double SILENT = 0.001;

	private final AudioWorld world;

	public LiveSteps(AudioWorld world) {
		this.world = world;
	}

	private TGSynthModel synthesizer() {
		return (TGSynthModel) MidiPlayer.getInstance(world.fixture().context).getSynthesizerProxy().getMidiSynthesizer();
	}

	// the output's left side over the seconds from now, while the threads TuxGuitar started run; then they are waited
	// for and the synthesizer's channels close
	private float[] sounded(double seconds) throws InterruptedException, MidiPlayerException {
		List<Thread> threads = world.threads().run();
		try {
			return LiveOutput.render(synthesizer(), (int) (seconds * TestInterface.SAMPLE_RATE))[0];
		} finally {
			for (Thread thread : threads) {
				thread.join();
			}
			synthesizer().closeChannels();
		}
	}

	// the clicks on the side of the output the song sounded live (see Meters.clicks)
	private List<Integer> clicks(int side) {
		float[] samples = world.live()[side];
		return Meters.clicks(samples, 0, samples.length);
	}

	@Then("the output's right side holds {int} clicks, each within {int} samples of a click on its left side")
	public void rightClicksWithTheLeftOnes(int count, int within) {
		List<Integer> right = clicks(1);
		List<Integer> left = clicks(0);
		assertEquals(count, right.size(), "right " + right);
		right.forEach(click -> assertTrue(left.stream().anyMatch(other -> Math.abs(other - click) <= within), "right " + right + " left " + left));
	}

	@Then("the output's left side holds {int} clicks and no {double} Hz tone above -60 dBFS")
	public void leftClicksAndNoTone(int count, double frequency) {
		float[] left = world.live()[0];
		assertEquals(count, Meters.clicks(left, 0, left.length).size(), "clicks " + Meters.clicks(left, 0, left.length));
		assertTrue(Meters.level(left, 0, left.length, frequency, TestInterface.SAMPLE_RATE) < SILENT);
	}

	// the tone starts sounding within the seconds, then holds the frequency for as long as it sounds in twice that time
	@Then("the output sounds a {int} Hz tone within 1 Hz within {double} seconds of entering it")
	public void soundsAToneWithinSecondsOfEntering(int frequency, double seconds) throws InterruptedException, MidiPlayerException {
		float[] output = sounded(2 * seconds);
		int[] above = IntStream.range(0, output.length).filter(i -> Math.abs(output[i]) >= SILENT).toArray();
		int first = (above.length > 0 ? above[0] : output.length);
		assertTrue(first < seconds * TestInterface.SAMPLE_RATE, "sounds from sample " + first);
		assertEquals(frequency, Meters.frequency(output, first, above[above.length - 1] + 1, TestInterface.SAMPLE_RATE), 1.0);
	}
}
