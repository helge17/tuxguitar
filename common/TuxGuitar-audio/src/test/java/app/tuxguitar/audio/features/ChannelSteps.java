package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableAddTrackController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackGenericController;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.gm.GMChannelRouterConfigurator;
import app.tuxguitar.player.base.MidiInstrument;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGMessagesManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on channels: which track plays on which, their General MIDI program, and the MIDI channels they are routed to.
 */
public class ChannelSteps {

	private final AudioWorld world;

	public ChannelSteps(AudioWorld world) {
		this.world = world;
	}

	// the MIDI channels the song's channels get, as a MIDI Port or a MIDI file routes them
	private GMChannelRouter route() {
		GMChannelRouter router = new GMChannelRouter();
		new GMChannelRouterConfigurator(router).configureRouter(world.song().getChannels());
		return router;
	}

	// each new track gets a new channel; a channel takes two MIDI channels while there are, then one
	@Given("I have added guitar tracks, each on a channel of its own, until no MIDI channel is free")
	public void addGuitarTracksUntilNoMidiChannelIsFree() {
		while (!route().getFreeChannels().isEmpty()) {
			world.executeUndoable(new TGAddNewTrackAction(world.fixture().context), new TGUndoableAddTrackController());
		}
		assertTrue(world.song().countChannels() < GMChannelRouter.MAX_CHANNELS);
	}

	@Then("the last track's channel is given no MIDI channel")
	public void theLastTrackSChannelIsGivenNoMidiChannel() {
		assertNull(route().getRoute(world.lastTrack().getChannelId()));
	}

	@When("I set track {int}'s channel to the channel of track {int}")
	public void setTrackChannel(int number, int other) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, world.channel(world.track(other)));
		world.executeUndoable(new TGSetTrackChannelAction(world.fixture().context), new TGUndoableTrackGenericController());
	}

	@When("I set the General MIDI program of track {int}'s channel to {string}")
	public void setTheGeneralMidiProgram(int number, String program) {
		updateChannel(number, TGChannel.DEFAULT_BANK, program(program));
	}

	@When("I make track {int}'s channel a percussion channel")
	public void makeAPercussionChannel(int number) {
		updateChannel(number, TGChannel.DEFAULT_PERCUSSION_BANK, TGChannel.DEFAULT_PERCUSSION_PROGRAM);
	}

	// the General MIDI program with this name
	private static short program(String name) {
		MidiInstrument[] instruments = TGMessagesManager.getInstruments();
		return (short) IntStream.range(0, instruments.length).filter(i -> instruments[i].getName().equals(name)).findFirst().getAsInt();
	}

	private void updateChannel(int number, short bank, short program) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, world.channel(world.track(number)));
		world.fixture().actionContext.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, bank);
		world.fixture().actionContext.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, program);
		world.executeUndoable(new TGUpdateChannelAction(world.fixture().context), new TGUndoableModifyChannelController());
	}
}
