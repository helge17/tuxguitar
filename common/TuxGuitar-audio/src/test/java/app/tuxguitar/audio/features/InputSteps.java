package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import app.tuxguitar.audio.TGInputMeter;
import app.tuxguitar.audio.action.TGAddAudioTrackAction;
import app.tuxguitar.audio.action.TGResetClipIndicatorAction;
import app.tuxguitar.audio.action.TGSetInputChannelsAction;
import app.tuxguitar.audio.undo.TGUndoableOnSuccess;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.song.models.TGAudioInput;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on what an audio track records from: its input channel(s), and its input level, metered while TuxGuitar is
 * stopped as the Test Interface delivers the player's signal.
 */
public class InputSteps {

	private final AudioWorld world;

	public InputSteps(AudioWorld world) {
		this.world = world;
	}

	// "input channel 2" (mono) or "input channels 1 and 2" (stereo: left, then right), as picked
	@ParameterType("input channels? \\d+(?: and \\d+)?")
	public int[] inputs(String text) {
		return Arrays.stream(text.replaceAll("[^\\d]+", " ").trim().split(" ")).mapToInt(Integer::parseInt).toArray();
	}

	// a change of the song: the app undoes it as a channel change
	@When("I set track {int} to record {inputs}")
	public void setInputChannels(int number, int[] inputChannels) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, world.channel(world.track(number)));
		world.fixture().actionContext.setAttribute(TGSetInputChannelsAction.ATTRIBUTE_INPUT_CHANNELS, inputChannels);
		world.executeUndoable(new TGSetInputChannelsAction(world.fixture().context), new TGUndoableOnSuccess(new TGUndoableModifyChannelController()));
	}

	@Given("I have set track {int} to record {inputs}")
	public void haveSetInputChannels(int number, int[] inputChannels) {
		setInputChannels(number, inputChannels);
		assertTrue(world.succeeded());
	}

	// an audio track is added recording one input channel; then it records the pair
	@Given("I have added an audio track recording input channels {int} and {int}")
	public void haveAddedAStereoAudioTrack(int left, int right) {
		world.fixture().actionContext.setAttribute(TGAddAudioTrackAction.ATTRIBUTE_INPUT_CHANNEL, left);
		world.execute(new TGAddAudioTrackAction(world.fixture().context));
		haveSetInputChannels(world.song().countTracks(), new int[] {left, right});
	}

	@Then("track {int} records input channels {int} and {int}")
	public void recordsInputChannels(int number, int left, int right) {
		TGAudioInput input = world.channel(world.track(number)).getAudioInput();
		assertEquals(left + " and " + right, input.getChannel(0) + " and " + input.getChannel(1));
	}

	// the device's clock runs while TuxGuitar is stopped: the input delivers that many seconds of its signal
	@When("the input has delivered {int} second(s)")
	public void theInputHasDelivered(int seconds) {
		world.session().listen(world.fixture().songManager, world.song(), seconds * world.device().getSampleRate());
	}

	private TGInputMeter meter(int number) {
		return world.session().getInputMeter(world.track(number));
	}

	@Then("track {int}'s input level reads {double} dBFS within 0.01 dB")
	public void inputLevelReads(int number, double level) {
		assertEquals(level, meter(number).getLevel(), 0.01);
	}

	@Then("track {int}'s input is clipping")
	public void isClipping(int number) {
		assertTrue(meter(number).isClipping());
	}

	@Then("track {int}'s input is not clipping")
	public void isNotClipping(int number) {
		assertFalse(meter(number).isClipping());
	}

	@Then("track {int}'s input has clipped")
	public void hasClipped(int number) {
		assertTrue(meter(number).hasClipped());
	}

	@Then("track {int}'s input has not clipped")
	public void hasNotClipped(int number) {
		assertFalse(meter(number).hasClipped());
	}

	@When("I reset track {int}'s clip indicator")
	public void resetTheClipIndicator(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.execute(new TGResetClipIndicatorAction(world.fixture().context));
	}
}
