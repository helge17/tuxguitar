package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import app.tuxguitar.audio.action.TGSetInputChannelsAction;
import app.tuxguitar.audio.undo.TGUndoableOnSuccess;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.song.models.TGAudioInput;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

/**
 * Steps on what an audio track records from: its input channel(s).
 */
public class InputSteps {

	private final AudioWorld world;

	public InputSteps(AudioWorld world) {
		this.world = world;
	}

	// "input channel 2" (mono) or "input channels 1 and 2" (stereo: left, then right)
	@ParameterType("input channels? \\d+(?: and \\d+)?")
	public TGAudioInput inputs(String text) {
		int[] channels = Arrays.stream(text.replaceAll("[^\\d]+", " ").trim().split(" ")).mapToInt(Integer::parseInt).toArray();
		return (channels.length == 1 ? TGAudioInput.mono(channels[0]) : TGAudioInput.stereo(channels[0], channels[1]));
	}

	// a change of the song: the app undoes it as a channel change
	@When("I set track {int} to record {inputs}")
	public void setInputChannels(int number, TGAudioInput input) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, world.channel(world.track(number)));
		world.fixture().actionContext.setAttribute(TGSetInputChannelsAction.ATTRIBUTE_INPUT, input);
		world.executeUndoable(new TGSetInputChannelsAction(world.fixture().context), new TGUndoableOnSuccess(new TGUndoableModifyChannelController()));
	}

	@Given("I have set track {int} to record {inputs}")
	public void haveSetInputChannels(int number, TGAudioInput input) {
		setInputChannels(number, input);
		assertTrue(world.succeeded());
	}
}
