package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.audio.action.TGAddAudioTrackAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.undo.impl.track.TGUndoableCloneTrackController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableMoveTrackUpController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableRemoveTrackController;
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
		theLastTrackIsAnAudioTrack();
	}

	@Then("the last track is an audio track")
	public void theLastTrackIsAnAudioTrack() {
		assertTrue(world.lastTrack().isAudio());
	}

	@When("I remove track {int}")
	public void removeTrack(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.executeUndoable(new TGRemoveTrackAction(world.fixture().context), new TGUndoableRemoveTrackController());
	}

	@Given("I have removed track {int}")
	public void haveRemovedTrack(int number) {
		int tracks = world.song().countTracks();
		removeTrack(number);
		assertEquals(tracks - 1, world.song().countTracks());
	}

	@When("I clone track {int}")
	public void cloneTrack(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.executeUndoable(new TGCloneTrackAction(world.fixture().context), new TGUndoableCloneTrackController());
	}

	@Given("I have cloned track {int}")
	public void haveClonedTrack(int number) {
		int tracks = world.song().countTracks();
		cloneTrack(number);
		assertEquals(tracks + 1, world.song().countTracks());
	}

	@When("I move track {int} up")
	public void moveTrackUp(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.executeUndoable(new TGMoveTrackUpAction(world.fixture().context), new TGUndoableMoveTrackUpController());
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
		assertEquals(inputChannel, world.channel(world.track(number)).getAudioInput().getChannel(0));
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

	@Given("I have muted track {int}")
	public void mute(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.fixture().actionContext.setAttribute(TGSetTrackMuteAction.ATTRIBUTE_MUTE, Boolean.TRUE);
		world.execute(new TGSetTrackMuteAction(world.fixture().context));
		assertTrue(world.track(number).isMute());
	}

	@Given("I have soloed track {int}")
	public void solo(int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.fixture().actionContext.setAttribute(TGSetTrackSoloAction.ATTRIBUTE_SOLO, Boolean.TRUE);
		world.execute(new TGSetTrackSoloAction(world.fixture().context));
		assertTrue(world.track(number).isSolo());
	}
}
