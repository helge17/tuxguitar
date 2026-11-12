package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.tuxguitar.audio.Meters;
import app.tuxguitar.audio.TGPlayedMeasure;
import app.tuxguitar.audio.TGTakes;
import app.tuxguitar.audio.TGTimeline;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeSegment;
import app.tuxguitar.song.models.TGTakeClip;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;

/**
 * Steps that listen to what playing or recording sounded: a track's tap (where it joins the output mix) or the output,
 * measured as the features README defines it.
 */
public class ListeningSteps {

	private static final int FADE = 256;
	// -60 dBFS
	private static final double SILENT = 0.001;

	private final AudioWorld world;

	public ListeningSteps(AudioWorld world) {
		this.world = world;
	}

	private Heard heard() {
		return PlaySteps.heard(world);
	}

	private float[] tap(int number) {
		return heard().tap(world.track(number));
	}

	// measures: "2", "1, 3 and 4"
	@ParameterType("\\d+(?:(?:, | and )\\d+)*")
	public List<Integer> measures(String text) {
		List<Integer> measures = new ArrayList<Integer>();
		for (String measure : text.split(", | and ")) {
			measures.add(Integer.valueOf(measure));
		}
		return measures;
	}

	// a tone per measure, or nothing: "220, 660, nothing and 550"
	@ParameterType("(?:\\d+|nothing)(?:(?:, | and )(?:\\d+|nothing))*")
	public List<String> tones(String text) {
		return Arrays.asList(text.split(", | and "));
	}

	// the transition starts on its first sample and reaches the new level on its last, each within 1 sample
	private static void assertTransition(float[] samples, int first, int last, float from, float to) {
		float near = Math.abs(to - from) / 512;
		int begins = first - 3;
		while (begins < samples.length - 1 && Math.abs(samples[begins] - from) <= near) {
			begins++;
		}
		int ends = begins;
		while (ends < samples.length - 1 && Math.abs(samples[ends] - to) > near) {
			ends++;
		}
		assertEquals(first, begins, 1, "where it starts going from " + from);
		assertEquals(last, ends, 1, "where it gets to " + to);
	}

	@Then("track {int} goes from {float} to {float} over the 256 samples from {position}, within 1 sample")
	public void goesOverTheSamplesFrom(int number, float from, float to, SongPosition position) {
		int at = heard().sampleAt(position, 0);
		assertTransition(tap(number), at, at + FADE - 1, from, to);
	}

	@Then("track {int} goes/fades from {float} to {float} over the 256 samples before {position}, within 1 sample")
	public void goesOverTheSamplesBefore(int number, float from, float to, SongPosition position) {
		int at = heard().sampleAt(position, 0);
		assertTransition(tap(number), at - FADE, at - 1, from, to);
	}

	@Then("track {int} fades from {float} to {float} over the last 256 samples of the first pass, within 1 sample")
	public void fadesAtTheEndOfTheFirstPass(int number, float from, float to) {
		int end = heard().passStart(1);
		assertTransition(tap(number), end - FADE, end - 1, from, to);
	}

	@Then("track {int}'s first sample of the second pass is {float}")
	public void firstSampleOfTheSecondPass(int number, float value) {
		assertEquals(value, tap(number)[heard().passStart(1)], 0.000001f);
	}

	@Then("track {int}'s sample at {position} is {float}")
	public void sampleAt(int number, SongPosition position, float value) {
		assertEquals(value, tap(number)[heard().sampleAt(position, 0)], 0.000001f);
	}

	@Then("track {int}'s first sample above 0 is {float}, at {position} within 1 sample")
	public void firstSampleAboveZero(int number, float value, SongPosition position) {
		float[] tap = tap(number);
		int first = 0;
		while (first < tap.length - 1 && tap[first] <= 0) {
			first++;
		}
		assertEquals(value, tap[first], 0.000001f);
		assertEquals(heard().sampleAt(position, 0), first, 1);
	}

	@Then("no two consecutive samples of track {int} differ by more than {float} from {position} to {position}")
	public void noJump(int number, float most, SongPosition from, SongPosition to) {
		float[] tap = tap(number);
		for (int i = heard().sampleAt(from, 0) + 1; i < heard().sampleAt(to, 0); i++) {
			assertTrue(Math.abs(tap[i] - tap[i - 1]) <= most, "samples " + (i - 1) + " and " + i + ": " + tap[i - 1] + ", " + tap[i]);
		}
	}

	// each measure sounds its tone within 1%, and none of the others above -60 dBFS; "nothing" is no tone above -60
	// dBFS. The FADE samples at each end of a measure are left out: crossfades and fades lie there
	@Then("track {int} sounds {tones} Hz in measures {int} to {int}")
	public void soundsInMeasures(int number, List<String> tones, int first, int last) {
		assertEquals(last - first + 1, tones.size());
		for (int m = first; m <= last; m++) {
			int[] measure = heard().measure(m);
			assertTone(tap(number), measure[0] + FADE, measure[1] - FADE, tones.get(m - first), tones, "measure " + m);
		}
	}

	private static void assertTone(float[] samples, int from, int to, String tone, List<String> tones, String where) {
		if (tone.equals("nothing")) {
			assertTrue(Meters.peak(samples, from, to) < SILENT, where + ": peak " + Meters.peak(samples, from, to));
			return;
		}
		double frequency = Double.parseDouble(tone);
		assertEquals(frequency, Meters.frequency(samples, from, to, TestInterface.SAMPLE_RATE), frequency / 100, where);
		for (String other : new ArrayList<String>(tones)) {
			if (!other.equals("nothing") && !other.equals(tone)) {
				double level = Meters.level(samples, from, to, Double.parseDouble(other), TestInterface.SAMPLE_RATE);
				assertTrue(level < SILENT, where + ": " + other + " Hz at " + level);
			}
		}
	}

	@Then("the output holds a {int} Hz tone above -60 dBFS")
	public void theOutputHoldsATone(int frequency) {
		assertTrue(outputLevel(frequency) >= SILENT, "level " + outputLevel(frequency));
	}

	@Then("the output holds no {int} Hz tone above -60 dBFS")
	public void theOutputHoldsNoTone(int frequency) {
		assertTrue(outputLevel(frequency) < SILENT, "level " + outputLevel(frequency));
	}

	private double outputLevel(int frequency) {
		float[] left = heard().left();
		float[] right = heard().right();
		return Math.max(Meters.level(left, 0, left.length, frequency, TestInterface.SAMPLE_RATE), Meters.level(right, 0, right.length, frequency, TestInterface.SAMPLE_RATE));
	}

	@Then("track {int} sounds {int} Hz for the first {int} beats of measure {int} and nothing for the rest of it")
	public void soundsForTheFirstBeats(int number, int frequency, int beats, int measure) {
		int[] bounds = heard().measure(measure);
		long beat = world.song().getMeasureHeader(measure - 1).getTimeSignature().getDenominator().getTime();
		int sounding = heard().sampleIn(measure, beats * beat);
		String tone = Integer.toString(frequency);
		assertTone(tap(number), bounds[0] + FADE, sounding - FADE, tone, Arrays.asList(tone), "the first " + beats + " beats");
		assertTone(tap(number), sounding, bounds[1], "nothing", Arrays.asList(tone), "the rest of measure " + measure);
	}

	@Then("track {int} sounds a {int} Hz tone within 1% in measure {int}")
	public void soundsAToneInMeasure(int number, int frequency, int measure) {
		int[] bounds = heard().measure(measure);
		String tone = Integer.toString(frequency);
		assertTone(tap(number), bounds[0] + FADE, bounds[1] - FADE, tone, Arrays.asList(tone), "measure " + measure);
	}

	// the beats of the measures played, where they sound: each played measure's beats from where it starts playing
	private List<Integer> beats() {
		TGTimeline timeline = heard().timeline();
		List<Integer> beats = new ArrayList<Integer>();
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure measure = timeline.get(i);
			long beat = measure.getHeader().getTimeSignature().getDenominator().getTime();
			for (long tick = 0; tick < measure.getHeader().getLength(); tick += beat) {
				if (tick >= measure.getFrom() && tick < measure.getTo()) {
					beats.add((int) timeline.sampleAt(i, tick));
				}
			}
		}
		return beats;
	}

	private static void assertOnBeats(List<Integer> clicks, List<Integer> beats, int within) {
		for (int click : clicks) {
			int nearest = beats.stream().min((a, b) -> Integer.compare(Math.abs(a - click), Math.abs(b - click))).get();
			assertEquals(nearest, click, within, "click at " + click + " of " + clicks);
		}
	}

	@Then("the output holds {int} clicks, each within 1 sample of a beat in song time")
	public void theOutputHoldsClicksOnTheBeats(int count) {
		List<Integer> clicks = Meters.clicks(heard().left(), 0, heard().left().length);
		assertEquals(count, clicks.size(), "clicks at " + clicks);
		assertOnBeats(clicks, beats(), 1);
	}

	@Then("track {int} sounds {int} clicks, each within 5 ms of a beat")
	public void soundsClicksOnTheBeats(int number, int count) {
		List<Integer> clicks = Meters.clicks(tap(number), 0, tap(number).length);
		assertEquals(count, clicks.size(), "clicks at " + clicks);
		assertOnBeats(clicks, beats(), TestInterface.SAMPLE_RATE / 200);
	}

	@Then("track {int} sounds its click at {position} within 5 ms")
	public void soundsItsClickAt(int number, SongPosition position) {
		List<Integer> clicks = Meters.clicks(tap(number), 0, tap(number).length);
		assertEquals(1, clicks.size(), "clicks at " + clicks);
		assertEquals(heard().sampleAt(position, 0), clicks.get(0), TestInterface.SAMPLE_RATE / 200);
	}

	// how long the track's takes play as the song plays: the samples of the played measures where a take plays
	@Then("track {int} plays for {double} seconds within 5 ms")
	public void playsFor(int number, double seconds) {
		TGTimeline timeline = heard().timeline();
		long samples = 0;
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure played = timeline.get(i);
			List<TGTakeSegment> segments = TGTakes.playback(world.track(number).getMeasure(played.getHeader().getNumber() - 1));
			for (int k = 0; k < segments.size(); k++) {
				long from = Math.max(segments.get(k).getStart(), played.getFrom());
				long to = Math.min(k + 1 < segments.size() ? segments.get(k + 1).getStart() : played.getHeader().getLength(), played.getTo());
				samples += (segments.get(k).getTake() != null && to > from ? timeline.sampleAt(i, to) - timeline.sampleAt(i, from) : 0);
			}
		}
		assertEquals(seconds, samples / (double) TestInterface.SAMPLE_RATE, 0.005);
	}

	@Then("track {int} sounds take {int} sample for sample in measure(s) {int}")
	public void soundsTheTakeInMeasure(int number, int take, int measure) {
		soundsTheTakeInMeasures(number, take, measure, measure);
	}

	// from the take's sample at the start of the first measure
	@Then("track {int} sounds take {int} sample for sample in measures {int} to {int}")
	public void soundsTheTakeInMeasures(int number, int take, int first, int last) {
		TGTake played = TGTakes.find(world.track(number), take);
		TGMeasure measure = world.track(number).getMeasure(first - 1);
		TGTakeClip clip = measure.getTakeClips().stream().filter(c -> c.getTake() == played).findFirst().get();
		float[] tap = tap(number);
		int from = heard().measure(first)[0];
		int to = heard().measure(last)[1];
		for (int i = from; i < to; i++) {
			assertEquals(played.getSample(clip.getFirstSample() + i - from), tap[i], 0f, "sample " + i);
		}
	}

	@Then("track {int} sounds {int} Hz then {int} Hz from beat {int} in measure {int}")
	public void soundsThenFromBeat(int number, int first, int second, int beat, int measure) {
		int[] bounds = heard().measure(measure);
		long beatTicks = world.song().getMeasureHeader(measure - 1).getTimeSignature().getDenominator().getTime();
		int change = heard().sampleIn(measure, (beat - 1) * beatTicks);
		List<String> tones = Arrays.asList(Integer.toString(first), Integer.toString(second));
		assertTone(tap(number), bounds[0] + FADE, change - FADE, tones.get(0), tones, "measure " + measure + " before beat " + beat);
		assertTone(tap(number), change + FADE, bounds[1] - FADE, tones.get(1), tones, "measure " + measure + " from beat " + beat);
	}

	@Then("the output holds a {int} Hz tone above -60 dBFS in measure(s) {measures}")
	public void theOutputHoldsAToneIn(int frequency, List<Integer> measures) {
		for (int measure : measures) {
			assertTrue(outputLevel(frequency, measure) >= SILENT, "measure " + measure + ": " + outputLevel(frequency, measure));
		}
	}

	@Then("the output holds no {int} Hz tone above -60 dBFS in measure(s) {measures}")
	public void theOutputHoldsNoToneIn(int frequency, List<Integer> measures) {
		for (int measure : measures) {
			assertTrue(outputLevel(frequency, measure) < SILENT, "measure " + measure + ": " + outputLevel(frequency, measure));
		}
	}

	// the measure, but for FADE samples at each end
	private double outputLevel(int frequency, int measure) {
		int[] bounds = heard().measure(measure);
		float[] left = heard().left();
		return Meters.level(left, bounds[0] + FADE, bounds[1] - FADE, frequency, TestInterface.SAMPLE_RATE);
	}

	@Then("track {int} sounds a {int} Hz tone within 1% in measures {int} to {int}")
	public void soundsAToneInMeasures(int number, int frequency, int first, int last) {
		for (int measure = first; measure <= last; measure++) {
			soundsAToneInMeasure(number, frequency, measure);
		}
	}

	@Then("track {int} sounds a {int} Hz tone and no {int} Hz tone above -60 dBFS in measures {measures}")
	public void soundsOneToneAndNotAnother(int number, int frequency, int other, List<Integer> measures) {
		for (int measure : measures) {
			int[] bounds = heard().measure(measure);
			assertTone(tap(number), bounds[0] + FADE, bounds[1] - FADE, Integer.toString(frequency), Arrays.asList(Integer.toString(frequency), Integer.toString(other)), "measure " + measure);
		}
	}

	// from the start of the first measure to the end of the last, but for the FADE samples at each end
	@Then("track {int} sounds a {int} Hz tone and no {int} Hz tone above -60 dBFS in measures {int} to {int}, but for 256 samples at each end")
	public void soundsOneToneAndNotAnotherBetween(int number, int frequency, int other, int first, int last) {
		int from = heard().measure(first)[0] + FADE;
		int to = heard().measure(last)[1] - FADE;
		assertTone(tap(number), from, to, Integer.toString(frequency), Arrays.asList(Integer.toString(frequency), Integer.toString(other)), "measures " + first + " to " + last);
	}
}
