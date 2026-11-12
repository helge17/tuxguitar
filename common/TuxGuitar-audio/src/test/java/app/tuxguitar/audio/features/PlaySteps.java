package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertTrue;

import app.tuxguitar.audio.TGRender;
import app.tuxguitar.audio.action.TGPlayAction;
import io.cucumber.java.en.When;

/**
 * Steps that play the song offline, on the Test Interface's clock, and keep what it sounded for the listening steps.
 */
public class PlaySteps {

	private final AudioWorld world;

	public PlaySteps(AudioWorld world) {
		this.world = world;
	}

	@When("I play the song from {position}")
	public void playFrom(SongPosition from) {
		play(from, Long.MAX_VALUE, 1);
	}

	@When("I play the song from {position} for {int} passes")
	public void playPasses(SongPosition from, int passes) {
		play(from, Long.MAX_VALUE, passes);
	}

	@When("I play the song from {position} to {position}")
	public void playTo(SongPosition from, SongPosition to) {
		play(from, to.tick(world.song()), 1);
	}

	private void play(SongPosition from, long stop, int passes) {
		world.playAlong(from.tick(world.song()), stop, passes);
		world.fixture().actionContext.setAttribute(TGPlayAction.ATTRIBUTE_START, from.tick(world.song()));
		world.fixture().actionContext.setAttribute(TGPlayAction.ATTRIBUTE_STOP, stop);
		world.fixture().actionContext.setAttribute(TGPlayAction.ATTRIBUTE_PASSES, passes);
		world.execute(new TGPlayAction(world.fixture().context));
		assertTrue(world.succeeded());
	}

	static Heard heard(AudioWorld world) {
		TGRender render = world.fixture().actionContext.getAttribute(TGPlayAction.ATTRIBUTE_RENDER);
		return new Heard(render, world.song());
	}
}
