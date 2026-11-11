package app.tuxguitar.editor.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.io.tg.TGSongWriterImpl;
import app.tuxguitar.resource.TGResourceLoader;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.managers.TGSongManager;
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
 */
public class TestTemplateManager {

	@TempDir
	Path userConfig;

	private final Map<String, byte[]> resources = new HashMap<String, byte[]>();
	private final TGContext context = new TGContext();
	private final TGTemplateManager templateManager;

	public TestTemplateManager() {
		TGResourceManager.getInstance(context).setResourceLoader(new TGResourceLoader() {
			public <T> Class<T> loadClass(String name) {
				return null;
			}

			public InputStream getResourceAsStream(String name) {
				byte[] bytes = resources.get(name);
				return (bytes == null ? null : new ByteArrayInputStream(bytes));
			}

			public URL getResource(String name) {
				return null;
			}

			public Enumeration<URL> getResources(String name) {
				return null;
			}
		});
		templateManager = new TGTemplateManager(context);
	}

	private static byte[] song(String name) throws TGFileFormatException {
		TGSongManager songManager = new TGSongManager();
		TGSong song = songManager.newSong();
		song.setName(name);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		TGSongWriterHandle handle = new TGSongWriterHandle();
		handle.setFactory(songManager.getFactory());
		handle.setSong(song);
		handle.setOutputStream(bytes);
		new TGSongWriterImpl().write(handle);
		return bytes.toByteArray();
	}

	// a bundled template, "template-default<suffix>.tg", holding a song named after the suffix
	private void bundled(String suffix) throws TGFileFormatException {
		resources.put("templates/template-default" + suffix + ".tg", song("default" + suffix));
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
		Files.write(userTemplate(), song("user"));

		assertEquals("user", newSong(Locale.FRENCH));
	}

	@Test
	public void aCorruptedUserTemplateIsDeleted() throws IOException {
		Files.write(userTemplate(), "not a song".getBytes(StandardCharsets.UTF_8));

		newSong(Locale.FRENCH);

		assertFalse(Files.exists(userTemplate()));
	}

	@Test
	public void afterACorruptedUserTemplateNoBundledTemplateIsFound() throws IOException {
		bundled("");
		Files.write(userTemplate(), "not a song".getBytes(StandardCharsets.UTF_8));

		assertEquals("none", newSong(Locale.FRENCH));
	}
}
