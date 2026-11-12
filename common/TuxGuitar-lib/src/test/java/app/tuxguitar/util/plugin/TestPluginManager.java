package app.tuxguitar.util.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.resource.TGResourceException;
import app.tuxguitar.resource.TGResourceLoader;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;

/**
 * Pins how TGPluginManager looks up its plugins: the TGPlugin providers listed in the service resources, kept in
 * order when they have a module id. A provider that cannot be created is reported as a lookup error and skipped;
 * when the service resources cannot be read at all, the lookup error is reported and no plugin is found. Looking
 * up again replaces the plugins found before.
 *
 * Plugins are read back by module id, errors as "message: cause type".
 */
public class TestPluginManager {

	public static class Alpha extends ModulePlugin {
		public Alpha() {
			super("alpha");
		}
	}

	public static class Beta extends ModulePlugin {
		public Beta() {
			super("beta");
		}
	}

	public static class Unnamed extends ModulePlugin {
		public Unnamed() {
			super(null);
		}
	}

	public abstract static class ModulePlugin implements TGPlugin {
		private final String moduleId;

		protected ModulePlugin(String moduleId) {
			this.moduleId = moduleId;
		}

		public String getModuleId() {
			return this.moduleId;
		}

		public void connect(TGContext context) {
		}

		public void disconnect(TGContext context) {
		}
	}

	@TempDir
	File folder;

	private final TGContext context = new TGContext();
	private final List<URL> services = new ArrayList<URL>();
	private final List<String> errors = new ArrayList<String>();

	private final TGResourceLoader classPath = new TGResourceLoader() {
		@SuppressWarnings("unchecked")
		public <T> Class<T> loadClass(String name) throws TGResourceException {
			try {
				return (Class<T>) TestPluginManager.class.getClassLoader().loadClass(name);
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

		public Enumeration<URL> getResources(String name) {
			return Collections.enumeration(TestPluginManager.this.services);
		}
	};

	public TestPluginManager() {
		TGErrorManager.getInstance(this.context).addErrorHandler(error -> this.errors.add(error.getMessage() + ": " + error.getCause().getClass().getName()));
	}

	private void providers(String... names) throws IOException {
		File file = new File(this.folder, "services");
		Files.write(file.toPath(), String.join("\n", names).getBytes(StandardCharsets.UTF_8));
		this.services.clear();
		this.services.add(file.toURI().toURL());
		TGResourceManager.getInstance(this.context).setResourceLoader(this.classPath);
	}

	private static List<String> moduleIds(TGPluginManager manager) {
		return manager.getPlugins().stream().map(TGPlugin::getModuleId).collect(Collectors.toList());
	}

	@Test
	public void findsTheListedPluginsThatHaveAModuleId() throws IOException {
		providers(Beta.class.getName(), Unnamed.class.getName(), Alpha.class.getName());

		assertEquals(List.of("beta", "alpha"), moduleIds(TGPluginManager.getInstance(this.context)));
		assertEquals(List.of(), this.errors);
	}

	@Test
	public void reportsAndSkipsAProviderThatIsNotAPlugin() throws IOException {
		providers(Alpha.class.getName(), String.class.getName(), Beta.class.getName());

		assertEquals(List.of("alpha", "beta"), moduleIds(TGPluginManager.getInstance(this.context)));
		assertEquals(List.of("An error occurred when trying to lookup plugin: java.util.NoSuchElementException"), this.errors);
	}

	@Test
	public void reportsServicesThatCannotBeRead() {
		assertEquals(List.of(), moduleIds(TGPluginManager.getInstance(this.context)));
		assertEquals(List.of("An error occurred when trying to lookup plugin: java.lang.NullPointerException"), this.errors);
	}

	@Test
	public void looksUpAgainFromScratch() throws IOException {
		providers(Alpha.class.getName());
		TGPluginManager manager = TGPluginManager.getInstance(this.context);
		providers(Beta.class.getName());

		manager.lookupPlugins();

		assertEquals(List.of("beta"), moduleIds(manager));
	}
}
