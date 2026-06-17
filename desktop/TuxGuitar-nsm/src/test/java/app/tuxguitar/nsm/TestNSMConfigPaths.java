package app.tuxguitar.nsm;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class TestNSMConfigPaths {

	private static final String SESSION_DIR = "/nsm/sessions/my-project.nTqXg1/tuxguitar";

	// NSMConfigPropertiesHandler(context, sessionDir) — context only used in
	// readProperties(); filePrefix() is pure string logic, so null is fine here.
	private final NSMConfigPropertiesHandler handler =
			new NSMConfigPropertiesHandler(null, SESSION_DIR);

	@Test
	public void testMainModuleGoesDirectlyInSessionDir() {
		String prefix = handler.filePrefix("tuxguitar");
		assertEquals(SESSION_DIR + File.separator, prefix);
	}

	@Test
	public void testPluginModuleGoesInPluginsSubdir() {
		String prefix = handler.filePrefix("tuxguitar-fluidsynth");
		assertEquals(SESSION_DIR + File.separator + "plugins" + File.separator, prefix);
	}

	@Test
	public void testAnotherPluginModule() {
		String prefix = handler.filePrefix("tuxguitar-jack");
		assertEquals(SESSION_DIR + File.separator + "plugins" + File.separator, prefix);
	}

	@Test
	public void testMainAndPluginPrefixesAreDifferent() {
		assertNotEquals(handler.filePrefix("tuxguitar"),
				handler.filePrefix("tuxguitar-fluidsynth"));
	}
}
