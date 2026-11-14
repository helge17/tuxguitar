package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import app.tuxguitar.audio.Meters;
import app.tuxguitar.audio.TGDrumTap;
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction;
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction;
import app.tuxguitar.player.base.MidiClick;
import app.tuxguitar.player.base.MidiPlayer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * Steps on Count In and the click sound. Count In clicks are counted on the drum tap, as the features README measures
 * them: each click's position is the sample on which TuxGuitar sends its note to the drums, and each must sound there,
 * and in the output, above -60 dBFS within its beat (a drum's attack is no click, so the position is not measured
 * from the audio).
 */
public class CountInSteps {

	// -60 dBFS
	private static final double SILENT = 0.001;

	private final AudioWorld world;

	public CountInSteps(AudioWorld world) {
		this.world = world;
	}

	private MidiPlayer player() {
		return MidiPlayer.getInstance(world.fixture().context);
	}

	@Given("I have turned Count In on")
	public void turnCountInOn() {
		world.execute(new TGTransportCountDownAction(world.fixture().context));
		assertTrue(player().getCountDown().isEnabled());
	}

	@Given("I have turned the Metronome on")
	public void turnTheMetronomeOn() {
		world.execute(new TGTransportMetronomeAction(world.fixture().context));
		assertTrue(player().isMetronomeEnabled());
	}

	// as the transport dialog sets them
	@Given("I have turned Count In on with {int} Count In ticks")
	public void turnCountInOnWithTicks(int ticks) {
		turnCountInOn();
		player().getCountDown().setTickCount(ticks);
	}

	// as the settings set it
	@Given("I have set the click sound to drum note {int} at velocity {int}")
	public void setTheClickSound(int note, int velocity) {
		player().setClick(new MidiClick(note, velocity));
	}

	private Heard heard() {
		return PlaySteps.heard(world);
	}

	private long[] clicks() {
		return heard().timeline().getCountIn().positions();
	}

	// each click sounds above -60 dBFS on the drum tap and in the output, from its position for a beat (or until the
	// output ends)
	private void assertEachSounds(long[] clicks, long beat) {
		for (long click : clicks) {
			int from = (int) click;
			int to = (int) Math.min(click + beat, heard().left().length);
			assertTrue(Math.max(Meters.peak(heard().drums(0), from, to), Meters.peak(heard().drums(1), from, to)) >= SILENT, "click at " + click + " on the drums");
			assertTrue(Math.max(Meters.peak(heard().left(), from, to), Meters.peak(heard().right(), from, to)) >= SILENT, "click at " + click + " in the output");
		}
	}

	@Then("the output holds {int} Count In clicks, {double} samples apart within 1 sample")
	public void holdsClicksApart(int count, double apart) {
		long[] clicks = clicks();
		assertEquals(count, clicks.length, Arrays.toString(clicks));
		for (int i = 1; i < clicks.length; i++) {
			assertEquals(apart, clicks[i] - clicks[i - 1], 1.0, Arrays.toString(clicks));
		}
		assertEachSounds(clicks, Math.round(apart));
	}

	// a beat: as far apart as the first two clicks
	@Then("the output holds {int} Metronome clicks")
	public void holdsMetronomeClicks(int count) {
		long[] clicks = heard().metronome();
		assertEquals(count, clicks.length, Arrays.toString(clicks));
		assertEachSounds(clicks, clicks[1] - clicks[0]);
	}

	// track 1's clicks, in its tap (where it joins the mix, as the output has it)
	@Then("the output holds {int} Metronome clicks, each within 1 sample of the left click on its beat")
	public void holdsMetronomeClicksWithTheLeftOnes(int count) {
		holdsMetronomeClicks(count);
		float[] tap = heard().tap(world.track(1));
		List<Integer> left = Meters.clicks(tap, 0, tap.length);
		long[] clicks = heard().metronome();
		assertEquals(clicks.length, left.size(), "left clicks at " + left);
		for (int i = 0; i < clicks.length; i++) {
			assertEquals(clicks[i], left.get(i), 1.0, Arrays.toString(clicks) + " left " + left);
		}
	}

	@Then("the output holds {int} Count In clicks")
	public void holdsClicks(int count) {
		long[] clicks = clicks();
		assertEquals(count, clicks.length, Arrays.toString(clicks));
		assertEachSounds(clicks, clicks[1] - clicks[0]);
	}

	@Then("the output holds {int} Count In clicks, all before {position} of the first pass")
	public void holdsClicksBefore(int count, SongPosition position) {
		holdsClicks(count);
		long[] clicks = clicks();
		assertTrue(clicks[clicks.length - 1] < heard().sampleAt(position, 0), Arrays.toString(clicks));
	}

	@Then("the last Count In click sounds {int} samples before {position}, within 1 sample")
	public void theLastClickSoundsBefore(int samples, SongPosition position) {
		long[] clicks = clicks();
		assertEquals(samples, (double) (heard().sampleAt(position, 0) - clicks[clicks.length - 1]), 1.0, Arrays.toString(clicks));
	}

	// the drum tap is, sample for sample, the General MIDI drums sounding that note at that velocity on the clicks'
	// samples, which the default click does not sound
	@Then("every Count In click sounds drum note {int} at velocity {int}")
	public void everyClickSounds(int note, int velocity) {
		float[][] expected = TGDrumTap.render(world.fixture().context, heard().timeline(), new MidiClick(note, velocity));
		float[][] byDefault = TGDrumTap.render(world.fixture().context, heard().timeline(), MidiClick.DEFAULT);
		assertFalse(Arrays.equals(expected[0], byDefault[0]), "the default click sounds the same");
		assertArrayEquals(expected[0], heard().drums(0));
		assertArrayEquals(expected[1], heard().drums(1));
	}
}
