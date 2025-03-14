package app.tuxguitar.app.system.properties;

import app.tuxguitar.app.system.config.TGConfigDefaults;
import app.tuxguitar.app.system.config.TGConfigDefaultsPropertiesHandler;
import app.tuxguitar.app.system.config.TGConfigPropertiesHandler;
import app.tuxguitar.app.system.icons.TGSkinInfoHandler;
import app.tuxguitar.app.system.icons.TGSkinPropertiesHandler;
import app.tuxguitar.app.system.plugins.TGPluginInfoHandler;
import app.tuxguitar.app.system.plugins.TGPluginPropertiesHandler;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.plugin.TGPluginInfo;
import app.tuxguitar.util.plugin.TGPluginProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;

public class TGPropertiesAdapter {

	public static void initialize(TGContext context) {
		TGPropertiesManager tgPropertiesManager = TGPropertiesManager.getInstance(context);
		tgPropertiesManager.setPropertiesFactory(new TGPropertiesFactoryImpl());
		tgPropertiesManager.addPropertiesReader(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler(context));
		tgPropertiesManager.addPropertiesWriter(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler(context));
		tgPropertiesManager.addPropertiesReader(TGConfigDefaults.RESOURCE, new TGConfigDefaultsPropertiesHandler(context));
		tgPropertiesManager.addPropertiesReader(TGPluginInfo.RESOURCE, new TGPluginInfoHandler(context));
		tgPropertiesManager.addPropertiesReader(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler(context));
		tgPropertiesManager.addPropertiesWriter(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler(context));
		tgPropertiesManager.addPropertiesReader(TGSkinInfoHandler.RESOURCE, new TGSkinInfoHandler(context));
		tgPropertiesManager.addPropertiesReader(TGSkinPropertiesHandler.RESOURCE, new TGSkinPropertiesHandler(context));
	}
}
