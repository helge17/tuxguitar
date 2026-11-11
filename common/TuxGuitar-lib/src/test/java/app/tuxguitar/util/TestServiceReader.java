package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.resource.TGResourceException;
import app.tuxguitar.resource.TGResourceLoader;

/**
 * Pins how TGServiceReader finds the providers of a service: every "META-INF/services/<service>" resource lists one
 * class name per line, "#" starts a comment, blank lines are skipped. The resources are read in order; one that
 * cannot be read is reported (printed) and skipped. Each provider is created when the iterator reaches it.
 */
public class TestServiceReader {

	public interface Greeter {
		String greet();
	}

	public static class Hello implements Greeter {
		public String greet() {
			return "hello";
		}
	}

	public static class Hola implements Greeter {
		public String greet() {
			return "hola";
		}
	}

	@TempDir
	File folder;

	private final List<URL> resources = new ArrayList<URL>();
	private final List<String> requested = new ArrayList<String>();
	private TGResourceException resourcesFailure;
	private final ByteArrayOutputStream printed = new ByteArrayOutputStream();
	private PrintStream systemErr;

	private final TGResourceLoader loader = new TGResourceLoader() {
		@SuppressWarnings("unchecked")
		public <T> Class<T> loadClass(String name) throws TGResourceException {
			try {
				return (Class<T>) Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new TGResourceException(e);
			}
		}

		public InputStream getResourceAsStream(String name) {
			return null;
		}

		public URL getResource(String name) {
			return null;
		}

		public Enumeration<URL> getResources(String name) throws TGResourceException {
			requested.add(name);
			if (resourcesFailure != null) {
				throw resourcesFailure;
			}
			return Collections.enumeration(resources);
		}
	};

	@BeforeEach
	public void captureErrors() {
		this.systemErr = System.err;
		System.setErr(new PrintStream(this.printed, true));
	}

	@AfterEach
	public void restoreErrors() {
		System.setErr(this.systemErr);
	}

	private void resource(String name, String... lines) throws IOException {
		File file = new File(this.folder, name);
		Files.write(file.toPath(), String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
		this.resources.add(file.toURI().toURL());
	}

	private List<String> greetings() {
		List<String> greetings = new ArrayList<String>();
		Iterator<Greeter> providers = TGServiceReader.lookupProviders(Greeter.class, this.loader);
		while (providers.hasNext()) {
			greetings.add(providers.next().greet());
		}
		return greetings;
	}

	@Test
	public void createsTheProvidersListedInEveryServiceResource() throws IOException {
		resource("first", "# greeters", "app.tuxguitar.util.TestServiceReader$Hello", "", "   ");
		resource("second", "  app.tuxguitar.util.TestServiceReader$Hola  # spanish", "#app.tuxguitar.util.TestServiceReader$Hello");

		assertEquals(List.of("hello", "hola"), greetings());
		assertEquals(List.of("META-INF/services/app.tuxguitar.util.TestServiceReader$Greeter"), this.requested);
	}

	@Test
	public void skipsAServiceResourceThatCannotBeRead() throws IOException {
		this.resources.add(new File(this.folder, "missing").toURI().toURL());
		resource("present", "app.tuxguitar.util.TestServiceReader$Hola");

		assertEquals(List.of("hola"), greetings());
		assertTrue(this.printed.toString().contains("missing"));
	}

	@Test
	public void findsNothingWithoutServiceResources() {
		assertEquals(List.of(), greetings());
	}

	@Test
	public void findsNothingWhenTheServiceResourcesCannotBeListed() throws IOException {
		resource("services", "app.tuxguitar.util.TestServiceReader$Hello");
		this.resourcesFailure = new TGResourceException("unavailable");

		assertEquals(List.of(), greetings());
	}

	@Test
	public void needsAServiceAndALoader() {
		assertThrows(IllegalArgumentException.class, () -> TGServiceReader.lookupProviders(null, this.loader));
		assertThrows(IllegalArgumentException.class, () -> TGServiceReader.lookupProviders(Greeter.class, (TGResourceLoader) null));
	}

	@Test
	public void aProviderThatCannotBeCreatedEndsTheIteration() throws IOException {
		resource("services", "java.lang.String", "no.such.Provider");
		Iterator<Greeter> providers = TGServiceReader.lookupProviders(Greeter.class, this.loader);

		assertTrue(providers.hasNext());
		assertThrows(NoSuchElementException.class, providers::next);
		assertTrue(providers.hasNext());
		assertThrows(NoSuchElementException.class, providers::next);
		assertFalse(providers.hasNext());
		assertThrows(NoSuchElementException.class, providers::next);
		assertThrows(UnsupportedOperationException.class, providers::remove);
	}
}
