package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.audio.TGTakeSpan;
import app.tuxguitar.audio.TGTakes;
import io.cucumber.java.en.Then;

/**
 * Steps on the takes of audio tracks: reading them, choosing and deleting them.
 */
public class TakeSteps {

	private final AudioWorld world;

	public TakeSteps(AudioWorld world) {
		this.world = world;
	}

	// newest first: "take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"
	@Then("track {int}'s takes read {string}")
	public void takesRead(int number, String expected) {
		List<String> takes = new ArrayList<String>();
		for (TGTakeSpan span : TGTakes.list(world.track(number))) {
			takes.add("take " + span.getTake().getNumber() + ": " + Positions.read(world.song(), span.getStart()) + " to " + Positions.read(world.song(), span.getEnd()));
		}
		assertEquals(expected, String.join(", ", takes));
	}
}
