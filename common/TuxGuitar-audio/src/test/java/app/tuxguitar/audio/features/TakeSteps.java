package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongToDoubleFunction;

import app.tuxguitar.audio.TGPlayedMeasure;
import app.tuxguitar.audio.TGRecording;
import app.tuxguitar.audio.TGTimeline;
import app.tuxguitar.audio.TGSongTime;
import app.tuxguitar.audio.TGTakeSpan;
import app.tuxguitar.audio.TGTakes;
import app.tuxguitar.audio.action.TGChooseTakeAction;
import app.tuxguitar.audio.action.TGDeleteTakeAction;
import app.tuxguitar.audio.undo.TGUndoableOnSuccess;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackGenericController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTakeSegment;
import app.tuxguitar.song.models.TGTrack;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on the takes of audio tracks: putting takes on a track, the player's signals, reading takes and what plays
 * where, choosing and deleting takes.
 */
public class TakeSteps {

	private final AudioWorld world;

	public TakeSteps(AudioWorld world) {
		this.world = world;
	}

	@ParameterType(Signals.PATTERN)
	public String signal(String text) {
		return text;
	}

	// as if recorded at the song's tempo, from the player playing the signal on the track's input
	@Given("track {int} has a take of {signal} from {position} to {position}")
	public void hasATake(int number, String signal, SongPosition from, SongPosition to) {
		TestInterface device = new TestInterface();
		device.setSampleRate(world.device().getSampleRate());
		List<TGPlayedMeasure> played = TGSongTime.play(world.song(), new MidiPlayerMode(), from.tick(world.song()), to.tick(world.song()), 1);
		device.setPlayed(new TGTimeline(world.song(), played, device.getSampleRate()));
		device.play(world.channel(world.track(number)).getAudioInput().getChannel(0), Signals.parse(signal, world.song(), device, from.measure()));
		TGRecording recording = new TGRecording(world.fixture().songManager, world.song(), device, null);
		recording.record(List.of(world.track(number)), played);
		world.rememberTakes(number);
	}

	// "<signal> for N samples, then <signal>, on input channel N" closes the sequence with a comma
	@Given("the player plays {signal}(,) on input channel {int}")
	public void thePlayerPlays(String signal, int inputChannel) {
		world.device().play(inputChannel, Signals.parse(signal, world.song(), world.device(), 1));
	}

	@Given("the player plays {signal} on input channel {int} on the first pass and {signal} on the second")
	public void thePlayerPlaysPerPass(String first, int inputChannel, String second) {
		LongToDoubleFunction onFirst = Signals.parse(first, world.song(), world.device(), 1);
		LongToDoubleFunction onSecond = Signals.parse(second, world.song(), world.device(), 1);
		world.device().play(inputChannel, sample -> (world.device().passAt(sample) == 0 ? onFirst : onSecond).applyAsDouble(sample));
	}

	// newest first: "take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"
	@Then("track {int}'s takes read {string}")
	public void takesRead(int number, String expected) {
		List<String> takes = new ArrayList<String>();
		for (TGTakeSpan span : TGTakes.list(world.track(number))) {
			takes.add("take " + span.getTake().getNumber() + ": " + Positions.read(world.song(), span.getStart()) + " to " + Positions.read(world.song(), span.getEnd()));
		}
		assertEquals(expected, String.join(", ", takes));
	}

	// measure by measure, "m1: take 1, m2: none"; a measure where the take changes inside it, by beat: "m2b1: take 1, m2b3: take 2"
	@Then("track {int}'s playback reads {string}")
	public void playbackReads(int number, String expected) {
		List<String> measures = new ArrayList<String>();
		TGTrack track = world.track(number);
		for (int index = 0; index < track.countMeasures(); index++) {
			measures.add(playback(track.getMeasure(index)));
		}
		assertEquals(expected, String.join(", ", measures));
	}

	private String playback(TGMeasure measure) {
		List<TGTakeSegment> segments = TGTakes.playback(measure);
		if (segments.size() == 1) {
			return "m" + measure.getNumber() + ": " + take(segments.get(0));
		}
		List<String> parts = new ArrayList<String>();
		for (TGTakeSegment segment : segments) {
			parts.add(Positions.read(world.song(), measure.getStart() + segment.getStart()) + ": " + take(segment));
		}
		return String.join(", ", parts);
	}

	private static String take(TGTakeSegment segment) {
		return (segment.getTake() == null ? "none" : "take " + segment.getTake().getNumber());
	}

	@When("I choose take {int} of track {int} for measures {int} to {int}")
	public void choose(int take, int number, int first, int last) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.fixture().actionContext.setAttribute(TGChooseTakeAction.ATTRIBUTE_TAKE, take);
		world.fixture().actionContext.setAttribute(TGChooseTakeAction.ATTRIBUTE_FIRST_MEASURE, first);
		world.fixture().actionContext.setAttribute(TGChooseTakeAction.ATTRIBUTE_LAST_MEASURE, last);
		world.executeUndoable(new TGChooseTakeAction(world.fixture().context), new TGUndoableOnSuccess(new TGUndoableTrackGenericController()));
	}

	@Given("I have chosen take {int} of track {int} for measures {int} to {int}")
	public void haveChosen(int take, int number, int first, int last) {
		choose(take, number, first, last);
		assertTrue(world.succeeded());
	}

	@When("I delete take {int} of track {int}")
	public void delete(int take, int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.fixture().actionContext.setAttribute(TGChooseTakeAction.ATTRIBUTE_TAKE, take);
		world.executeUndoable(new TGDeleteTakeAction(world.fixture().context), new TGUndoableOnSuccess(new TGUndoableTrackGenericController()));
	}

	@Given("I have deleted take {int} of track {int}")
	public void haveDeleted(int take, int number) {
		delete(take, number);
		assertTrue(world.succeeded());
	}

	@Then("take {int} of track {int} is sample for sample what it was")
	public void isSampleForSampleWhatItWas(int take, int number) {
		assertArrayEquals(world.rememberedSamples(number, take), AudioWorld.samples(TGTakes.find(world.track(number), take)));
	}
}
