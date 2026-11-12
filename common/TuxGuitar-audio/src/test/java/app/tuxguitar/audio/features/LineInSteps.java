package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.tuxguitar.audio.Meters;
import app.tuxguitar.audio.TGMonitoring;
import app.tuxguitar.audio.TGPlayedMeasure;
import app.tuxguitar.audio.TGTakeSpan;
import app.tuxguitar.audio.TGTakes;
import app.tuxguitar.audio.TGTimeline;
import app.tuxguitar.audio.action.TGArmTrackAction;
import app.tuxguitar.audio.action.TGDisarmTrackAction;
import app.tuxguitar.audio.action.TGSetMonitoringAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps of recording line-in: the audio device's rate, the Percentage of Tempo and Training Mode, arming and
 * monitoring, and what a take holds, measured on its samples.
 */
public class LineInSteps {

	private static final double SILENT = 0.001;

	private final AudioWorld world;

	public LineInSteps(AudioWorld world) {
		this.world = world;
	}

	@Given("I have set the audio device to {string} at {int} Hz")
	public void setTheAudioDeviceAt(String name, int sampleRate) {
		world.device().setSampleRate(sampleRate);
		world.session().setDevice(world.device());
	}

	private MidiPlayerMode mode() {
		return MidiPlayer.getInstance(world.fixture().context).getMode();
	}

	@Given("I have set the Percentage of Tempo to {int}")
	public void setThePercentageOfTempo(int percent) {
		mode().setType(MidiPlayerMode.TYPE_SIMPLE);
		mode().setSimplePercent(percent);
		mode().reset();
	}

	// as the Play Mode dialog applies it: the mode starts at its first percentage
	@Given("I have turned Training Mode on from {int}% to {int}% by {int}% over measures {int} to {int}")
	public void turnTrainingModeOn(int from, int to, int increment, int first, int last) {
		mode().setType(MidiPlayerMode.TYPE_CUSTOM);
		mode().setCustomPercentFrom(from);
		mode().setCustomPercentTo(to);
		mode().setCustomPercentIncrement(increment);
		mode().setLoop(true);
		mode().setLoopSHeader(first);
		mode().setLoopEHeader(last);
		mode().reset();
	}

	@When("I arm track {int}")
	public void arm(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.execute(new TGArmTrackAction(world.fixture().context));
	}

	@Given("I have disarmed track {int}")
	public void disarm(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.execute(new TGDisarmTrackAction(world.fixture().context));
		assertFalse(world.session().isArmed(world.track(number)));
	}

	@Given("I have set track {int}'s monitoring to {word}")
	public void setMonitoring(int number, String monitoring) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.fixture().actionContext.setAttribute(TGSetMonitoringAction.ATTRIBUTE_MONITORING, TGMonitoring.valueOf(monitoring.toUpperCase(Locale.ROOT)));
		world.execute(new TGSetMonitoringAction(world.fixture().context));
		assertTrue(world.succeeded());
	}

	@Then("track {int}'s monitoring is {word}")
	public void monitoringIs(int number, String monitoring) {
		assertEquals(monitoring.toUpperCase(Locale.ROOT), world.session().getMonitoring(world.track(number)).name());
	}

	// where what was played ended: the end of the last measure played
	@Then("the recording has stopped by itself at {position}")
	public void stoppedByItselfAt(SongPosition position) {
		TGTimeline timeline = PlaySteps.heard(world).timeline();
		TGPlayedMeasure last = timeline.get(timeline.count() - 1);
		assertEquals(position.tick(world.song()), last.getHeader().getStart() + last.getTo());
	}

	private TGTake take(int take, int number) {
		return TGTakes.find(world.track(number), take);
	}

	private float[] samples(int take, int number) {
		return AudioWorld.samples(take(take, number));
	}

	@Then("take {int} of track {int} is {int} samples long")
	public void isSamplesLong(int take, int number, int length) {
		assertEquals(length, take(take, number).countSamples());
	}

	@Then("takes {int}, {int} and {int} of track {int} are {int}, {int} and {int} samples long")
	public void areSamplesLong(int first, int second, int third, int number, int firstLength, int secondLength, int thirdLength) {
		assertEquals(List.of(firstLength, secondLength, thirdLength),
			List.of(take(first, number).countSamples(), take(second, number).countSamples(), take(third, number).countSamples()));
	}

	@Then("take {int} of track {int} holds a click at sample {int} within 1 sample")
	public void holdsAClickAt(int take, int number, int sample) {
		List<Integer> clicks = Meters.clicks(samples(take, number), 0, take(take, number).countSamples());
		assertTrue(clicks.stream().anyMatch(click -> Math.abs(click - sample) <= 1), "clicks at " + clicks);
	}

	// the beats the take covers, on its samples, at the tempo it was recorded at (the Percentage of Tempo included)
	private static List<Integer> beats(TGTake take, TGTrack track) {
		List<Integer> beats = new ArrayList<Integer>();
		for (int m = 0; m < track.countMeasures(); m++) {
			TGMeasure measure = track.getMeasure(m);
			long beat = measure.getHeader().getTimeSignature().getDenominator().getTime();
			for (TGTakeClip clip : measure.getTakeClips()) {
				for (long tick = 0; clip.getTake() == take && tick < clip.getEnd(); tick += beat) {
					if (tick >= clip.getStart()) {
						beats.add(clip.sampleAt(tick));
					}
				}
			}
		}
		return beats;
	}

	// "", " at 80%" or " at the Percentage it was recorded at": a take's beats are always at the tempo it was
	// recorded at
	@Then("take {int} of track {int} holds {int} clicks, each within 1 sample of a beat{}")
	public void holdsClicksOnBeats(int take, int number, int count, String atPercentage) {
		assertClicksOnBeats(take(take, number), world.track(number), count);
	}

	@Then("every take of track {int} holds {int} clicks, each within 1 sample of a beat at the Percentage it was recorded at")
	public void everyTakeHoldsClicksOnBeats(int number, int count) {
		for (TGTakeSpan span : TGTakes.list(world.track(number))) {
			assertClicksOnBeats(span.getTake(), world.track(number), count);
		}
	}

	private static void assertClicksOnBeats(TGTake take, TGTrack track, int count) {
		float[] samples = AudioWorld.samples(take);
		List<Integer> clicks = Meters.clicks(samples, 0, samples.length);
		List<Integer> beats = beats(take, track);
		assertEquals(count, clicks.size(), "take " + take.getNumber() + ": clicks at " + clicks + ", beats at " + beats);
		for (int click : clicks) {
			assertTrue(beats.stream().anyMatch(beat -> Math.abs(beat - click) <= 1), "click at " + click + ", beats at " + beats);
		}
	}

	@Then("take {int} of track {int} holds {int} clicks and no {int} Hz tone")
	public void holdsClicksAndNoTone(int take, int number, int count, int frequency) {
		float[] samples = samples(take, number);
		assertEquals(count, Meters.clicks(samples, 0, samples.length).size());
		assertTrue(level(samples, frequency) < SILENT);
	}

	// no clicks: no jump of more than 0.1 between two samples
	@Then("take {int} of track {int} holds a {int} Hz tone and no clicks")
	public void holdsAToneAndNoClicks(int take, int number, int frequency) {
		float[] samples = samples(take, number);
		assertEquals(frequency, Meters.frequency(samples, 0, samples.length, rate()), frequency / 100.0);
		for (int i = 1; i < samples.length; i++) {
			assertTrue(Math.abs(samples[i] - samples[i - 1]) <= 0.1, "a jump at sample " + i);
		}
	}

	@Then("take {int} of track {int} holds a {int} Hz tone peaking at {float} within {float}")
	public void holdsAPeakingTone(int take, int number, int frequency, float peak, float within) {
		float[] samples = samples(take, number);
		assertEquals(frequency, Meters.frequency(samples, 0, samples.length, rate()), frequency / 100.0);
		assertEquals(peak, Meters.peak(samples, 0, samples.length), within);
	}

	@Then("take {int} of track {int} holds a {int} Hz tone within 1% and no {int} Hz tone above -60 dBFS")
	public void holdsAToneAndNotAnother(int take, int number, int frequency, int other) {
		float[] samples = samples(take, number);
		assertEquals(frequency, Meters.frequency(samples, 0, samples.length, rate()), frequency / 100.0);
		assertTrue(level(samples, other) < SILENT, other + " Hz at " + level(samples, other));
	}

	@Then("take {int} of track {int} is sample for sample take {int} of track {int}")
	public void isSampleForSample(int take, int number, int other, int otherNumber) {
		assertArrayEquals(samples(other, otherNumber), samples(take, number));
	}

	private double level(float[] samples, int frequency) {
		return Meters.level(samples, 0, samples.length, frequency, rate());
	}

	private int rate() {
		return world.device().getSampleRate();
	}
}
