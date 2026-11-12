package app.tuxguitar.editor.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.editor.InMemoryResources;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * Pins how TGTemplateManager loads the templates listed in "templates/templates.xml", and how it picks the template
 * of a new song: the user template when there is one, else the most specific of the bundled default templates for the
 * language, then country, then variant of the locale. A user template that cannot be read as a song is deleted, and
 * then (the template being still marked as the user's) the bundled templates are looked for as files in the working
 * directory, so none is found.
 *
 * The bundled templates are served from memory, each a song named after its locale; the result is read back as the
 * name of the song, or "none". A locale without country (or variant) looks for no template ending in "_" for it.
 * A template that cannot be read is printed to System.err, captured here.
 */
public class TestTemplateManager {

	@TempDir
	Path userConfig;

	private final ByteArrayOutputStream printed = new ByteArrayOutputStream();
	private PrintStream systemErr;

	private final TGContext context = new TGContext();
	private final InMemoryResources resources = new InMemoryResources(context);
	private final TGTemplateManager templateManager;

	public TestTemplateManager() {
		templateManager = new TGTemplateManager(context);
	}

	@BeforeEach
	public void captureSystemErr() {
		systemErr = System.err;
		System.setErr(new PrintStream(printed, true));
	}

	@AfterEach
	public void restoreSystemErr() {
		System.setErr(systemErr);
	}

	// a bundled template, "template-default<suffix>.tg", holding a song named after the suffix
	private void bundled(String suffix) throws TGFileFormatException {
		resources.put("templates/template-default" + suffix + ".tg", InMemoryResources.song("default" + suffix));
	}

	private Path userTemplate() {
		return userConfig.resolve("user-template.tg");
	}

	private String newSong(Locale locale) {
		TGSong song = templateManager.getDefaultTemplateAsSong(userTemplate().toString(), locale);
		return (song == null ? "none" : song.getName());
	}

	@Test
	public void theTemplatesListedInTheConfigurationAreLoaded() {
		resources.put("templates/templates.xml",
				"<templates><template resource=\"template-1.tg\" name=\"Blues Trio\"/></templates>".getBytes(StandardCharsets.UTF_8));

		templateManager.loadTemplates();

		assertEquals("Blues Trio", templateManager.getTemplates().next().getName());
	}

	@Test
	public void withoutTemplatesThereIsNoSong() {
		assertEquals("none", newSong(Locale.FRENCH));
	}

	@Test
	public void eachLocaleGetsItsMostSpecificTemplate() throws TGFileFormatException {
		for (String suffix : new String[] { "", "_fr", "_fr_", "_fr_CA", "_fr_CA_", "_fr_CA_var" }) {
			bundled(suffix);
		}

		assertEquals("default", newSong(Locale.GERMAN));
		assertEquals("default_fr", newSong(Locale.FRENCH));
		assertEquals("default_fr_CA", newSong(new Locale("fr", "CA")));
		assertEquals("default_fr_CA_var", newSong(new Locale("fr", "CA", "var")));
	}

	@Test
	public void theUserTemplateComesFirst() throws IOException {
		bundled("_fr");
		Files.write(userTemplate(), InMemoryResources.song("user"));

		assertEquals("user", newSong(Locale.FRENCH));
	}

	@Test
	public void aCorruptedUserTemplateIsDeleted() throws IOException {
		Files.write(userTemplate(), "not a song".getBytes(StandardCharsets.UTF_8));

		newSong(Locale.FRENCH);

		assertFalse(Files.exists(userTemplate()));
		assertTrue(printed.toString().startsWith(TGFileFormatException.class.getName() + ": "));
	}

	@Test
	public void afterACorruptedUserTemplateNoBundledTemplateIsFound() throws IOException {
		bundled("");
		Files.write(userTemplate(), "not a song".getBytes(StandardCharsets.UTF_8));

		assertEquals("none | true | true", newSong(Locale.FRENCH)
				+ " | " + printed.toString().contains("FileNotFoundException: template-default.tg ")
				+ " | " + printed.toString().contains("FileNotFoundException: template-default_fr.tg "));
	}
}
