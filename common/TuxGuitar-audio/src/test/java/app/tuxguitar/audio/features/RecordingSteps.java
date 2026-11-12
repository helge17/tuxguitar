package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.tuxguitar.audio.TGPunchRange;
import app.tuxguitar.audio.action.TGArmTrackAction;
import app.tuxguitar.audio.action.TGDisarmTrackAction;
import app.tuxguitar.audio.action.TGRecordAction;
import app.tuxguitar.audio.action.TGSetPunchRangeAction;
import app.tuxguitar.audio.undo.TGUndoableRecordingController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps that arm tracks, set the punch range and record. A recording runs offline on the Test Interface's clock, from
 * a position until it reaches another, the song ends, or the passes of Play looped are done.
 */
public class RecordingSteps {

	private final AudioWorld world;
	private List<Object> historyBeforeRecording;
	private Thread recording;

	public RecordingSteps(AudioWorld world) {
		this.world = world;
	}

	@ParameterType(SongPosition.PATTERN)
	public SongPosition position(String text) {
		return SongPosition.parse(text);
	}

	@Given("I have armed track {int}")
	public void arm(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.execute(new TGArmTrackAction(world.fixture().context));
		trackIsArmed(number);
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
	public RecordingStop stop(String to, String passes, String inTheSecondPass) {
		return RecordingStop.of(to, passes, inTheSecondPass);
	}

	@When("I record from {position} {stop}")
	public void record(SongPosition from, RecordingStop stop) {
		historyBeforeRecording = world.undoHistory();
		long start = from.tick(world.song());
		world.playAlong(start, stop.tick(world.song()), stop.passes());
		world.fixture().actionContext.setAttribute(TGRecordAction.ATTRIBUTE_START, start);
		world.fixture().actionContext.setAttribute(TGRecordAction.ATTRIBUTE_STOP, stop.tick(world.song()));
		world.fixture().actionContext.setAttribute(TGRecordAction.ATTRIBUTE_PASSES, stop.passes());
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
