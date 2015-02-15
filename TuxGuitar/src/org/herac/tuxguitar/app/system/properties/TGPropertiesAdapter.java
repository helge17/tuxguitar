package org.herac.tuxguitar.app.system.properties;

import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigDefaultsPropertiesHandler;
import org.herac.tuxguitar.app.system.config.TGConfigPropertiesHandler;
import org.herac.tuxguitar.app.system.plugins.TGPluginInfoHandler;
import org.herac.tuxguitar.app.system.plugins.TGPluginPropertiesHandler;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;
import org.herac.tuxguitar.util.plugin.TGPluginInfo;
import org.herac.tuxguitar.util.plugin.TGPluginProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;

public class TGPropertiesAdapter {
	
	public static void initialize(TGContext context) {
		TGPropertiesManager tgPropertiesManager = TGPropertiesManager.getInstance(context);
		tgPropertiesManager.setPropertiesFactory(new TGPropertiesFactoryImpl());
		tgPropertiesManager.addPropertiesReader(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler());
		tgPropertiesManager.addPropertiesWriter(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler());
		tgPropertiesManager.addPropertiesReader(TGConfigDefaults.RESOURCE, new TGConfigDefaultsPropertiesHandler());
		tgPropertiesManager.addPropertiesReader(TGPluginInfo.RESOURCE, new TGPluginInfoHandler());
		tgPropertiesManager.addPropertiesReader(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler());
		tgPropertiesManager.addPropertiesWriter(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler());
	}
}
