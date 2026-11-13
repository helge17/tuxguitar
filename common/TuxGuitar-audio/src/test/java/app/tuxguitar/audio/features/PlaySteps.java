package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertTrue;

import app.tuxguitar.audio.LivePlay;
import app.tuxguitar.audio.TGRender;
import app.tuxguitar.audio.action.TGPlayAction;
import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.impl.sequencer.MidiSequencerImpl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

/**
 * Steps that play the song offline, on the Test Interface's clock, and keep what it sounded for the listening steps.
 */
public class PlaySteps {

	private final AudioWorld world;
	// where playing under way started (the When that happens while it plays plays it, see playUnderWay)
	private SongPosition playingFrom;

	public PlaySteps(AudioWorld world) {
		this.world = world;
	}

	@Given("I am playing the song from {position}")
	public void playingFrom(SongPosition from) {
		this.playingFrom = from;
	}

	// the playing under way, until the song ends, offline: what happens while it plays is scheduled before
	void playUnderWay() {
		playFrom(this.playingFrom);
	}

	@When("I play the song from {position}( until the song ends)")
	public void playFrom(SongPosition from) {
		play(from, Long.MAX_VALUE, 1);
	}

	// stopped there: what was sounding stops with it
	@Given("I have played the song from {position} and stopped at {position}")
	@When("I play the song from {position} to {position}")
	public void playTo(SongPosition from, SongPosition to) {
		play(from, to.tick(world.song()), 1);
	}

	@When("I play the song from {position} for {int} passes")
	public void playPasses(SongPosition from, int passes) {
		play(from, Long.MAX_VALUE, passes);
	}

	// live, as the player plays the song on the TuxGuitar Synthesizer's sample clock (see LivePlay), not offline: what the
	// output sounded is kept (see LiveSteps); then the synthesizer's channels close
	@When("I play the song live from {position}")
	public void playLive(SongPosition from) throws MidiPlayerException, TGAudioDeviceException {
		MidiPlayer player = MidiPlayer.getInstance(world.fixture().context);
		player.loadSequencer(new MidiSequencerImpl(world.fixture().context));
		player.setTickPosition(from.tick(world.song()));
		player.play();
		world.threads().runStartHere();
		world.setLive(LivePlay.play(world.fixture().context, player, synthesizer(player), world.device()));
		player.closeChannels();
	}

	// live, TuxGuitar stopped while the audio device runs (see LivePlay); then the synthesizer's channels close
	@When("the input has delivered {int} second(s) live")
	public void theInputHasDeliveredLive(int seconds) throws MidiPlayerException, TGAudioDeviceException {
		MidiPlayer player = MidiPlayer.getInstance(world.fixture().context);
		world.setLive(LivePlay.listen(world.fixture().context, synthesizer(player), world.device(), seconds * world.device().getSampleRate()));
		player.closeChannels();
	}

	private static TGSynthModel synthesizer(MidiPlayer player) {
		return (TGSynthModel) player.getSynthesizerProxy().getMidiSynthesizer();
	}

	private void play(SongPosition from, long stop, int passes) {
		world.playAlong(from.tick(world.song()), stop, passes);
		world.fixture().actionContext.setAttribute(TGPlayAction.ATTRIBUTE_START, from.tick(world.song()));
		world.fixture().actionContext.setAttribute(TGPlayAction.ATTRIBUTE_STOP, stop);
		world.fixture().actionContext.setAttribute(TGPlayAction.ATTRIBUTE_PASSES, passes);
		world.fixture().actionContext.setAttribute(TGPlayAction.ATTRIBUTE_STOP_SAMPLE, null);
		world.execute(new TGPlayAction(world.fixture().context));
		assertTrue(world.succeeded());
	}

	static Heard heard(AudioWorld world) {
		TGRender render = world.fixture().actionContext.getAttribute(TGPlayAction.ATTRIBUTE_RENDER);
		return new Heard(render, world.song());
	}
}
