package app.tuxguitar.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.util.TGContext;

/**
 * Pins how TGResourceBundle.getBundle merges the property files of a bundle ("lang.messages" is read from
 * lang/messages.properties): the default file, then one per part of the locale that is set
 * (messages_es, messages_es_AR, messages_es_AR_var), each overriding the keys of the ones before. A part that is not
 * set still adds its "_" to the names of the parts after it. Files are read as UTF-8.
 */
public class TestResourceBundle {

	@TempDir
	File folder;

	private void write(String name, String content) throws IOException {
		File file = new File(this.folder, name);
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	// a resource is the file of the same name in the folder
	private TGResourceBundle bundle(Locale locale) {
		TGContext context = new TGContext();
		TGResourceManager.getInstance(context).setResourceLoader(new TGResourceLoader() {
			public <T> Class<T> loadClass(String name) {
				return null;
			}

			public InputStream getResourceAsStream(String name) {
				return null;
			}

			public URL getResource(String name) {
				return null;
			}

			public Enumeration<URL> getResources(String name) throws TGResourceException {
				try {
					File file = new File(TestResourceBundle.this.folder, name);
					return Collections.enumeration(file.exists()
						? Collections.singletonList(file.toURI().toURL())
						: Collections.<URL>emptyList());
				} catch (IOException e) {
					throw new TGResourceException(e);
				}
			}
		});
		return TGResourceBundle.getBundle(context, "lang.messages", locale);
	}

	private static String describe(TGResourceBundle bundle) {
		return bundle.getString("a") + " " + bundle.getString("b") + " " + bundle.getString("c") + " "
			+ bundle.getString("d");
	}

	@Test
	public void eachPartOfTheLocaleOverridesTheOnesBefore() throws IOException {
		write("lang/messages.properties", "a=default\nb=default\nc=default\nd=default");
		write("lang/messages_es.properties", "b=es\nc=es\nd=es");
		write("lang/messages_es_AR.properties", "c=es_AR\nd=es_AR");
		write("lang/messages_es_AR_var.properties", "d=es_AR_var");

		assertEquals("default es es_AR es_AR_var", describe(bundle(new Locale("es", "AR", "var"))));
		assertEquals("default es es_AR es_AR", describe(bundle(new Locale("es", "AR"))));
		assertEquals("default es es es", describe(bundle(new Locale("es"))));
		assertEquals("default default default default", describe(bundle(new Locale("fr", "FR"))));
	}

	@Test
	public void aMissingPartKeepsItsSeparator() throws IOException {
		write("lang/messages.properties", "a=default\nb=default\nc=default\nd=default");
		write("lang/messages__AR.properties", "c=AR");
		write("lang/messages_es__v.properties", "d=es_v");
		// never read: an unset part is not loaded on its own
		write("lang/messages_.properties", "b=unset");

		assertEquals("default default AR default", describe(bundle(new Locale("", "AR"))));
		assertEquals("default default default es_v", describe(bundle(new Locale("es", "", "v"))));
	}

	@Test
	public void readsUtf8AndKeepsTheLocale() throws IOException {
		write("lang/messages.properties", "a=mañana");
		Locale locale = new Locale("es");
		TGResourceBundle bundle = bundle(locale);

		assertEquals("mañana", bundle.getString("a"));
		assertSame(locale, bundle.getLocale());
	}
}
