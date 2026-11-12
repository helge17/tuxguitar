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
import app.tuxguitar.util.properties.MapProperties;
import app.tuxguitar.util.properties.TGPropertiesException;
import app.tuxguitar.util.properties.TGPropertiesManager;

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

		public void connect(TGContext context) throws TGPluginException {
			CALLS.add("connect " + this.moduleId);
		}

		public void disconnect(TGContext context) throws TGPluginException {
			CALLS.add("disconnect " + this.moduleId);
		}
	}

	// a second plugin of module alpha
	public static class AlphaToo extends ModulePlugin {
		public AlphaToo() {
			super("alpha");
		}
	}

	public static class Early extends ModulePlugin implements TGEarlyInitPlugin {
		public Early() {
			super("early");
		}

		public void earlyInit(TGContext context) {
			CALLS.add("early init early");
		}
	}

	// fails every call with a plugin error
	public static class Refusing extends ModulePlugin implements TGEarlyInitPlugin {
		public Refusing() {
			super("refusing");
		}

		public void connect(TGContext context) throws TGPluginException {
			throw new TGPluginException("refused");
		}

		public void disconnect(TGContext context) throws TGPluginException {
			throw new TGPluginException("refused");
		}

		public void earlyInit(TGContext context) throws TGPluginException {
			throw new TGPluginException("refused");
		}
	}

	// fails every call with an unexpected error
	public static class Broken extends ModulePlugin implements TGEarlyInitPlugin {
		public Broken() {
			super("broken");
		}

		public void connect(TGContext context) {
			throw new IllegalStateException();
		}

		public void disconnect(TGContext context) {
			throw new IllegalStateException();
		}

		public void earlyInit(TGContext context) {
			throw new IllegalStateException();
		}
	}

	// what the plugins were asked to do, in order
	private static final List<String> CALLS = new ArrayList<String>();

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
		CALLS.clear();
		TGErrorManager.getInstance(this.context).addErrorHandler(error -> this.errors.add(error.getMessage() + ": "
				+ (error.getCause() == null ? "-" : error.getCause().getClass().getName())));
	}

	// plugin settings kept in memory, every plugin enabled but the given ones
	private void disabled(String... moduleIds) {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
		for (String moduleId : moduleIds) {
			TGPluginProperties.getInstance(this.context).setEnabled(moduleId, false);
		}
	}

	private TGPluginManager managerOf(String... providers) throws IOException {
		providers(providers);
		return TGPluginManager.getInstance(this.context);
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

	@Test
	public void connectsTheEnabledPluginsAndDisconnectsThemAll() throws IOException {
		disabled("beta");
		TGPluginManager manager = managerOf(Alpha.class.getName(), Beta.class.getName(), Early.class.getName());

		manager.connectEnabled();
		manager.disconnectAll();

		assertEquals(List.of("connect alpha", "connect early", "disconnect alpha", "disconnect beta", "disconnect early"), CALLS);
		assertEquals(List.of(), this.errors);
	}

	@Test
	public void initializesEarlyTheEnabledEarlyInitPluginsOnly() throws IOException {
		disabled("refusing");
		TGPluginManager manager = managerOf(Alpha.class.getName(), Early.class.getName(), Refusing.class.getName());

		manager.earlyInitPlugins();

		assertEquals(List.of("early init early"), CALLS);
		assertEquals(List.of(), this.errors);
	}

	@Test
	public void connectsAndDisconnectsEveryPluginOfAModule() throws IOException {
		TGPluginManager manager = managerOf(Alpha.class.getName(), Beta.class.getName(), AlphaToo.class.getName());

		manager.connectPlugins("alpha");
		manager.disconnectPlugins("alpha");

		assertEquals(List.of("connect alpha", "connect alpha", "disconnect alpha", "disconnect alpha"), CALLS);
	}

	@Test
	public void enablingAModuleSavesItAndConnectsIt() throws IOException {
		disabled("alpha");
		TGPluginManager manager = managerOf(Alpha.class.getName(), Beta.class.getName());

		manager.updatePluginStatus("alpha", true);
		boolean enabled = manager.isEnabled("alpha");
		manager.updatePluginStatus("beta", false);

		assertEquals(List.of("connect alpha", "disconnect beta"), CALLS);
		assertEquals(List.of(true, false), List.of(enabled, manager.isEnabled("beta")));
	}

	@Test
	public void aStatusThatCannotBeSavedIsReportedAndChangesNothing() throws IOException {
		TGPluginManager manager = managerOf(Alpha.class.getName());

		manager.updatePluginStatus("alpha", true);

		assertEquals(List.of(), CALLS);
		assertEquals(List.of("An error occurred when trying to set plugin status: java.lang.NullPointerException"), this.errors);
	}

	@Test
	public void aStatusThatCannotBeReadIsReportedAsDisabled() throws IOException {
		TGPluginManager manager = managerOf(Alpha.class.getName());
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(() -> {
			throw new TGPropertiesException("unreadable");
		});

		assertEquals(false, manager.isEnabled("alpha"));
		assertEquals(List.of("An error occurred when trying to get plugin status: " + TGPropertiesException.class.getName()), this.errors);
	}

	@Test
	public void pluginFailuresAreReportedAndUnexpectedOnesWrapped() throws IOException {
		TGPluginManager manager = managerOf(Refusing.class.getName(), Broken.class.getName());

		manager.connectEnabled();
		manager.earlyInitPlugins();
		manager.disconnectAll();

		assertEquals(List.of(
				"refused: -",
				"An error occurred when trying to connect plugin: java.lang.IllegalStateException",
				"refused: -",
				"An error occurred when trying to initialize plugin early: java.lang.IllegalStateException",
				"refused: -",
				"An error occurred when trying to disconnect plugin: java.lang.IllegalStateException"), this.errors);
	}

	@Test
	public void findsThePluginsOfAType() throws IOException {
		TGPluginManager manager = managerOf(Alpha.class.getName(), Early.class.getName(), Refusing.class.getName());

		assertEquals(List.of("early", "refusing"), manager.getPluginInstances(TGEarlyInitPlugin.class).stream()
				.map(TGPlugin::getModuleId).collect(Collectors.toList()));
		assertEquals(List.of(), manager.getPluginInstances(Beta.class));
	}
}
