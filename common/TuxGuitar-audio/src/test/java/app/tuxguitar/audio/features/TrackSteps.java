package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.audio.action.TGAddAudioTrackAction;
import app.tuxguitar.song.models.TGTrack;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on tracks and their channels: adding audio tracks, and reading what a track is and what it sounds through.
 */
public class TrackSteps {

	private final AudioWorld world;

	public TrackSteps(AudioWorld world) {
		this.world = world;
	}

	@When("I add an audio track recording input channel {int}")
	public void addAnAudioTrack(int inputChannel) {
		world.fixture().actionContext.setAttribute(TGAddAudioTrackAction.ATTRIBUTE_INPUT_CHANNEL, inputChannel);
		world.execute(new TGAddAudioTrackAction(world.fixture().context));
	}

	@Given("I have added an audio track recording input channel {int}")
	public void haveAddedAnAudioTrack(int inputChannel) {
		addAnAudioTrack(inputChannel);
		assertTrue(world.lastTrack().isAudio());
	}

	@Then("track {int} is an audio track with {int} strings")
	public void isAnAudioTrackWithStrings(int number, int strings) {
		isAnAudioTrack(number);
		assertEquals(strings, world.track(number).stringCount());
	}

	@Then("track {int} is an audio track")
	public void isAnAudioTrack(int number) {
		assertTrue(world.track(number).isAudio());
	}

	@Then("track {int} records input channel {int}")
	public void recordsInputChannel(int number, int inputChannel) {
		assertEquals(inputChannel, world.channel(world.track(number)).getAudioInput().getChannel());
	}

	@Then("track {int}'s sound chain reads {string}")
	public void soundChainReads(int number, String expected) {
		assertEquals(expected, TGSoundChain.read(world.channel(world.track(number))));
	}

	@Then("track {int}'s channel is not track {int}'s channel")
	public void channelIsNotChannelOf(int number, int other) {
		TGTrack track = world.track(number);
		assertNotEquals(world.track(other).getChannelId(), track.getChannelId());
		assertEquals(track.getChannelId(), world.channel(track).getChannelId());
	}
}
