package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.tuxguitar.audio.LivePlay;
import app.tuxguitar.audio.TGPunchRange;
import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.audio.action.TGArmTrackAction;
import app.tuxguitar.audio.action.TGDisarmTrackAction;
import app.tuxguitar.audio.action.TGRecordAction;
import app.tuxguitar.audio.action.TGSetPunchRangeAction;
import app.tuxguitar.audio.undo.TGUndoableRecordingController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.impl.sequencer.MidiSequencerImpl;
import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps that arm tracks, set the punch range and record. A recording runs offline on the Test Interface's clock, from
 * a position until it reaches another, the song ends, or the passes of Play looped are done; or live.
 */
public class RecordingSteps {

	// the block of the live device half a second in (blocks of 512 frames)
	private static final int HALF_A_SECOND = TestInterface.SAMPLE_RATE / 2 / 512;

	private final AudioWorld world;
	private final SongSteps songSteps;
	private List<Object> historyBeforeRecording;
	private Thread recording;

	public RecordingSteps(AudioWorld world, SongSteps songSteps) {
		this.world = world;
		this.songSteps = songSteps;
	}

	// live: the recording runs on a thread of its own, as the app runs it, while the live device runs a block at a time
	// and the player's play loop takes a turn after each (see LivePlay); it ends by itself at the end of the song, or
	// after the passes of Play looped (the player stopped where the last one loops). The change given is made half a
	// second in, while it records; then the synthesizer's channels close
	private void recordLive(SongPosition from, RecordingStop stop, Runnable change) throws TGAudioDeviceException, MidiPlayerException {
		historyBeforeRecording = world.undoHistory();
		MidiPlayer player = MidiPlayer.getInstance(world.fixture().context);
		player.loadSequencer(new MidiSequencerImpl(world.fixture().context));
		Map<String, Object> attributes = Map.<String, Object>of(TGRecordAction.ATTRIBUTE_START, from.tick(world.song()));
		int[] pass = {1};
		world.setLive(LivePlay.run(world.fixture().context, world.synthesizer(), world.backend(), () -> {
			recording = world.executeInBackground(new TGRecordAction(world.fixture().context), new TGUndoableRecordingController(), attributes);
			world.threads().runStartHere(recording);
		}, block -> {
			if (block == HALF_A_SECOND) {
				change.run();
			}
			if (player.isRunning() && !player.runPlayLoopProcess() && player.isRunning()) {
				pass[0] = nextPass(player, pass[0], stop.passes());
			}
			return recording.isAlive();
		}));
		player.closeChannels();
		for (int number = 1; number <= world.song().countTracks(); number++) {
			world.rememberTakes(number);
		}
	}

	// the play loop ended where a pass loops, the next pass kept to start: it starts, unless the passes are done
	private int nextPass(MidiPlayer player, int pass, int passes) {
		if (pass < passes) {
			world.threads().runStartHere();
		} else {
			player.stop();
		}
		return pass + 1;
	}

	@When("I record live from {position} {stop}")
	public void recordLive(SongPosition from, RecordingStop stop) throws TGAudioDeviceException, MidiPlayerException {
		recordLive(from, stop, () -> { });
	}

	@When("I record live from {position} {stop}, inserting a measure before measure {int} while it records")
	public void recordLiveInserting(SongPosition from, RecordingStop stop, int number) throws TGAudioDeviceException, MidiPlayerException {
		recordLive(from, stop, () -> songSteps.insertAMeasure(number));
	}

	@ParameterType(SongPosition.PATTERN)
	public SongPosition position(String text) {
		return SongPosition.parse(text);
	}

	@Given("I have armed track {int}")
	public void arm(int number) {
		runArm(number);
		trackIsArmed(number);
	}

	// the arm action on the track, which may be refused
	void runArm(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.execute(new TGArmTrackAction(world.fixture().context));
	}

	@Then("track {int} is armed")
	public void trackIsArmed(int number) {
		assertTrue(world.session().isArmed(world.track(number)));
	}

	@Then("track {int} is not armed")
	public void trackIsNotArmed(int number) {
		assertFalse(world.session().isArmed(world.track(number)));
	}

	@ParameterType(RecordingStop.PATTERN)
	public RecordingStop stop(String to, String passes, String inTheSecondPass, String afterTheSecondClick) {
		return RecordingStop.of(to, passes, inTheSecondPass, afterTheSecondClick);
	}

	@When("I record from {position} {stop}")
	public void record(SongPosition from, RecordingStop stop) {
		historyBeforeRecording = world.undoHistory();
		long start = from.tick(world.song());
		world.playAlong(start, stop.tick(world.song()), stop.passes());
		world.fixture().actionContext.setAttribute(TGRecordAction.ATTRIBUTE_START, start);
		world.fixture().actionContext.setAttribute(TGRecordAction.ATTRIBUTE_STOP, stop.tick(world.song()));
		world.fixture().actionContext.setAttribute(TGRecordAction.ATTRIBUTE_PASSES, stop.passes());
		int percent = MidiPlayer.getInstance(world.fixture().context).getMode().getCurrentPercent();
		world.fixture().actionContext.setAttribute(TGRecordAction.ATTRIBUTE_STOP_SAMPLE, stop.sample(from, world.song(), percent, world.device().getSampleRate()));
		world.executeUndoable(new TGRecordAction(world.fixture().context), new TGUndoableRecordingController());
		for (int number = 1; number <= world.song().countTracks(); number++) {
			world.rememberTakes(number);
		}
	}

	@Given("I have recorded from {position} {stop}")
	public void haveRecorded(SongPosition from, RecordingStop stop) {
		record(from, stop);
		assertTrue(world.succeeded());
	}

	// a recording from the position, with no stop, left under way: the device holds its first capture until the scenario
	// ends
	@Given("I am recording from {position}")
	public void recordingFrom(SongPosition from) throws InterruptedException {
		world.device().hold();
		world.playAlong(from.tick(world.song()), Long.MAX_VALUE, 1);
		recording = world.executeInBackground(new TGRecordAction(world.fixture().context), new TGUndoableRecordingController(),
			Map.<String, Object>of(TGRecordAction.ATTRIBUTE_START, from.tick(world.song()), TGRecordAction.ATTRIBUTE_STOP, Long.MAX_VALUE, TGRecordAction.ATTRIBUTE_PASSES, 1));
		assertTrue(world.device().awaitWaiting(recording), "the recording reached the device");
		assertTrue(world.session().isRecording());
	}

	@After
	public void stopTheRecording() throws InterruptedException {
		world.device().release();
		if (recording != null) {
			recording.join(TimeUnit.SECONDS.toMillis(TestInterface.WAIT_SECONDS));
		}
	}

	@When("I disarm track {int}")
	public void disarm(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.execute(new TGDisarmTrackAction(world.fixture().context));
	}

	@Then("the undo history is as it was before the recording")
	public void theUndoHistoryIsAsItWas() {
		assertEquals(historyBeforeRecording, world.undoHistory());
	}

	@When("I set the punch range to measures {int} to {int}")
	public void setThePunchRange(int first, int last) {
		world.fixture().actionContext.setAttribute(TGSetPunchRangeAction.ATTRIBUTE_FIRST_MEASURE, first);
		world.fixture().actionContext.setAttribute(TGSetPunchRangeAction.ATTRIBUTE_LAST_MEASURE, last);
		world.execute(new TGSetPunchRangeAction(world.fixture().context));
	}

	@Given("I have set the punch range to measures {int} to {int}")
	public void haveSetThePunchRange(int first, int last) {
		setThePunchRange(first, last);
		assertTrue(world.succeeded());
	}

	@Then("the punch range is measures {int} to {int}")
	public void thePunchRangeIs(int first, int last) {
		TGPunchRange range = world.session().getPunchRange(world.song());
		assertEquals(first + " to " + last, range.getFirst() + " to " + range.getLast());
	}

	@Then("there is no punch range")
	public void thereIsNoPunchRange() {
		assertNull(world.session().getPunchRange(world.song()));
	}

	@Given("I have turned Play looped on over measures {int} to {int}")
	public void turnPlayLoopedOn(int first, int last) {
		MidiPlayerMode mode = MidiPlayer.getInstance(world.fixture().context).getMode();
		mode.setLoop(true);
		mode.setLoopSHeader(first);
		mode.setLoopEHeader(last);
	}
}
