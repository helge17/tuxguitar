package app.tuxguitar.editor.features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * Steps on the song as a whole, shared by every feature: making it, setting up its track, and reading it back.
 */
public class SongSteps {

	private final SongWorld world;

	public SongSteps(SongWorld world) {
		this.world = world;
	}

	@Given("a new song with one guitar track")
	public void aNewSong() {
		world.newSong();
	}

	@Given("the track's max fret is {int}")
	public void theMaxFretIs(int maxFret) {
		world.track().setMaxFret(maxFret);
	}

	@Then("the caret's beat reads {string}")
	public void theCaretBeatReads(String expected) {
		assertEquals(expected, SongActionFixture.describe(world.caretBeat()));
	}

	@Then("the action succeeds")
	public void theActionSucceeds() {
		assertEquals(Boolean.TRUE, success());
	}

	@Then("the action is refused")
	public void theActionIsRefused() {
		assertEquals(null, success());
	}

	private Object success() {
		return world.fixture().actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS);
	}
}
