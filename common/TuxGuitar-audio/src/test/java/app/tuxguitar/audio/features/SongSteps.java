package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import app.tuxguitar.resource.TGResourceBundle;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGMessagesManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * Steps on the song as a whole: making it, and whether the last action succeeded.
 */
public class SongSteps {

	// TuxGuitar's English messages, which name the General MIDI programs; Maven runs the tests in the module folder
	private static final String MESSAGES = "../resources/lang/messages.properties";

	private final AudioWorld world;

	public SongSteps(AudioWorld world) {
		this.world = world;
	}

	@Given("a new song with one guitar track and {int} measures of 4\\/4 at {int} bpm")
	public void aNewSong(int measures, int bpm) throws IOException {
		loadMessages();
		world.fixture().measureCount(measures);
		Iterator<TGMeasureHeader> headers = world.song().getMeasureHeaders();
		while (headers.hasNext()) {
			headers.next().getTempo().setQuarterValue(bpm);
		}
	}

	private static void loadMessages() throws IOException {
		Properties properties = new Properties();
		try (InputStream in = new FileInputStream(MESSAGES)) {
			properties.load(in);
		}
		TGMessagesManager.getInstance().setResources(new TGResourceBundle(Locale.ENGLISH, properties));
	}

	@Then("the action succeeds")
	public void theActionSucceeds() {
		assertTrue(world.succeeded());
	}

	@Then("the action is refused")
	public void theActionIsRefused() {
		assertFalse(world.succeeded());
	}

	@Then("the song has {int} channel(s)")
	public void theSongHasChannels(int count) {
		assertEquals(count, world.song().countChannels());
	}
}
