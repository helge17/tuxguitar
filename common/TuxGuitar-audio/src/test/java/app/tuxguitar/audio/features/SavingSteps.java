package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.tg.TGSongReaderImpl;
import app.tuxguitar.io.tg.TGStream;
import app.tuxguitar.song.models.TGSong;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Saving the song as a TuxGuitar file and opening it again, as the app does (TGWriteSongAction, TGReadSongAction with
 * the format code "tg"), and what the file holds. A reader of file format 2.0 is today's reader without TuxGuitar's
 * audio support (no take codec), as older versions and Android read.
 */
public class SavingSteps {

	private static final String VERSION_PREFIX = "TuxGuitar_file_format ";
	// the node that holds the TuxGuitar release a file was written with
	private static final String TG_VERSION = "<TGVersion[^>]*/>";

	private final AudioWorld world;

	public SavingSteps(AudioWorld world) {
		this.world = world;
	}

	@Given("I have saved the song as a TuxGuitar file")
	@When("I save the song as a TuxGuitar file")
	public void save() {
		ByteArrayOutputStream file = new ByteArrayOutputStream();
		TGActionContext context = new TGActionContext() {
		};
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, world.song());
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, world.fixture().songManager);
		context.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, file);
		context.setAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT, TGStream.TG_FORMAT);
		TGActionManager.getInstance(world.fixture().context).execute(TGWriteSongAction.NAME, context);
		world.setSaved(file.toByteArray());
	}

	@Given("I have opened it again")
	@When("I open it again")
	public void openAgain() {
		world.open(world.saved());
	}

	@When("I open the format {double} file {string}")
	public void openTheFile(double version, String name) throws IOException {
		byte[] file = resource(name);
		assertEquals(VERSION_PREFIX + version, entries(file).get("version.txt"), name);
		world.open(file);
	}

	@Then("the song has {int} track and {int} measures")
	public void theSongHas(int tracks, int measures) {
		TGSong song = world.song();
		assertEquals(tracks + " " + measures, song.countTracks() + " " + song.countMeasureHeaders());
	}

	@Then("the file's format version is {double}")
	public void theFormatVersionIs(double version) throws IOException {
		assertEquals(VERSION_PREFIX + version, entries(world.saved()).get("version.txt"));
	}

	@Then("the file holds only {string} and {string}")
	public void theFileHoldsOnly(String first, String second) throws IOException {
		assertEquals(first + " " + second, String.join(" ", entries(world.saved()).keySet()));
	}

	@Then("its {string} is the golden file {string}, the TuxGuitar version aside")
	public void itIsTheGoldenFile(String entry, String golden) throws IOException {
		String expected = new String(resource(golden), StandardCharsets.UTF_8);
		assertEquals(expected.replaceAll(TG_VERSION, ""), entries(world.saved()).get(entry).replaceAll(TG_VERSION, ""));
	}

	// the message of the innermost cause of the refusal
	@Then("a reader of file format {double} refuses it with {string}")
	public void aReaderRefusesIt(double version, String message) {
		assertEquals(2.0, version, 0.0, "the only older reader kept");
		Throwable refusal = assertThrows(TGFileFormatException.class, () -> readAsFormat20(world.saved()));
		while (refusal.getCause() != null) {
			refusal = refusal.getCause();
		}
		assertEquals(message, refusal.getMessage());
	}

	static TGSong readAsFormat20(byte[] file) throws TGFileFormatException {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setFactory(new app.tuxguitar.song.factory.TGFactory());
		handle.setInputStream(new ByteArrayInputStream(file));
		new TGSongReaderImpl().read(handle);
		return handle.getSong();
	}

	// the file's entries, in order, by name, as text
	static Map<String, String> entries(byte[] file) throws IOException {
		Map<String, String> entries = new LinkedHashMap<String, String>();
		try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(file))) {
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				entries.put(entry.getName(), new String(zip.readAllBytes(), StandardCharsets.UTF_8));
			}
		}
		return entries;
	}

	// a file of the features folder
	private static byte[] resource(String name) throws IOException {
		try (InputStream in = SavingSteps.class.getResourceAsStream("/features/" + name)) {
			return in.readAllBytes();
		}
	}
}
