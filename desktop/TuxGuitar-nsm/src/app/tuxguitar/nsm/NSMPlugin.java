package app.tuxguitar.nsm;

import app.tuxguitar.app.system.properties.TGDefaultAndStoredPropertiesHandler;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.plugin.TGEarlyInitPlugin;
import app.tuxguitar.util.plugin.TGPluginException;
import app.tuxguitar.util.plugin.TGPluginProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;

import java.io.File;

public class NSMPlugin implements TGEarlyInitPlugin {

	public static final String MODULE_ID = "tuxguitar-nsm";

	private static final long OPEN_TIMEOUT_MS = 5000;

	// suffix used by TGPluginPropertiesHandler for the "plugin-settings" resource
	private static final String PLUGIN_SETTINGS_SUFFIX = "-" + TGPluginProperties.RESOURCE + ".cfg";

	private NSMClient nsmClient;

	@Override
	public String getModuleId() {
		return MODULE_ID;
	}

	/*
	 * earlyInit() is called before the UI event loop starts and before any
	 * TuxGuitar config file is read.  This gives us a narrow window to:
	 *   1. Contact the NSM server and discover the session directory.
	 *   2. Redirect TuxGuitar's config I/O to that directory.
	 *   3. Register the action interceptor that suppresses the default empty song.
	 *
	 * We block for up to OPEN_TIMEOUT_MS waiting for /nsm/client/open.  In
	 * practice the NSM server is local and responds in well under 1 ms.
	 */
	@Override
	public void earlyInit(TGContext context) throws TGPluginException {
		String nsmUrl = System.getenv("NSM_URL");
		if (nsmUrl == null || nsmUrl.isEmpty()) {
			return;
		}

		try {
			this.nsmClient = new NSMClient(context, nsmUrl);

			// Phase 1: start UDP communication and send /nsm/server/announce.
			this.nsmClient.startCommunication();

			// Phase 2: block until /nsm/client/open arrives (or timeout).
			String sessionDir = this.nsmClient.waitForSessionDirectory(OPEN_TIMEOUT_MS);

			if (sessionDir != null) {
				redirectConfig(context, sessionDir);
			}

			// Phase 3: register the action interceptor (must happen before
			// TGLoadTemplateAction is dispatched at the end of startUIContext).
			this.nsmClient.activateInterceptor();

		} catch (Exception e) {
			throw new TGPluginException(e);
		}
	}

	@Override
	public void connect(TGContext context) throws TGPluginException {
		// initialization is done in earlyInit
	}

	@Override
	public void disconnect(TGContext context) throws TGPluginException {
		if (this.nsmClient != null) {
			this.nsmClient.stop();
			this.nsmClient = null;
		}
	}

	// -------------------------------------------------------------------------

	/*
	 * Replace the standard config handlers in TGPropertiesManager with ones that
	 * read from / write to the NSM session directory.
	 *
	 * Two resources are redirected:
	 *
	 *  "config"          — main app config + per-plugin configs
	 *                      (TGConfigManager.RESOURCE)
	 *                      Session files:
	 *                        {sessionDir}/tuxguitar.cfg
	 *                        {sessionDir}/plugins/tuxguitar-fluidsynth.cfg  etc.
	 *
	 *  "plugin-settings" — which plugins are enabled/disabled
	 *                      (TGPluginProperties.RESOURCE)
	 *                      Session file:
	 *                        {sessionDir}/tuxguitar-plugin-settings.cfg
	 */
	private void redirectConfig(TGContext context, String sessionDir) {
		TGPropertiesManager mgr = TGPropertiesManager.getInstance(context);

		// --- "config" resource ---
		mgr.removePropertiesReader(TGConfigManager.RESOURCE);
		mgr.removePropertiesWriter(TGConfigManager.RESOURCE);
		NSMConfigPropertiesHandler configHandler = new NSMConfigPropertiesHandler(context, sessionDir);
		mgr.addPropertiesReader(TGConfigManager.RESOURCE, configHandler);
		mgr.addPropertiesWriter(TGConfigManager.RESOURCE, configHandler);

		// --- "plugin-settings" resource ---
		mgr.removePropertiesReader(TGPluginProperties.RESOURCE);
		mgr.removePropertiesWriter(TGPluginProperties.RESOURCE);
		TGDefaultAndStoredPropertiesHandler pluginSettingsHandler =
			new TGDefaultAndStoredPropertiesHandler(
				context,
				"",                                           // classpath defaults prefix
				PLUGIN_SETTINGS_SUFFIX,                       // classpath defaults suffix
				sessionDir + File.separator,                  // session dir prefix
				PLUGIN_SETTINGS_SUFFIX                        // session dir suffix
			);
		mgr.addPropertiesReader(TGPluginProperties.RESOURCE, pluginSettingsHandler);
		mgr.addPropertiesWriter(TGPluginProperties.RESOURCE, pluginSettingsHandler);
	}
}
