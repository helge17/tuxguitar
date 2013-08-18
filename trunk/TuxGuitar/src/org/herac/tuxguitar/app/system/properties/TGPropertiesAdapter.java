package org.herac.tuxguitar.app.system.properties;

import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigDefaultsPropertiesHandler;
import org.herac.tuxguitar.app.system.config.TGConfigPropertiesHandler;
import org.herac.tuxguitar.app.system.plugins.TGPluginInfoHandler;
import org.herac.tuxguitar.app.system.plugins.TGPluginPropertiesHandler;
import org.herac.tuxguitar.util.configuration.TGConfigManager;
import org.herac.tuxguitar.util.plugin.TGPluginInfo;
import org.herac.tuxguitar.util.plugin.TGPluginProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;

public class TGPropertiesAdapter {
	
	public static void initialize() {
		TGPropertiesManager.getInstance().setPropertiesFactory(new TGPropertiesFactoryImpl());
		TGPropertiesManager.getInstance().addPropertiesReader(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler());
		TGPropertiesManager.getInstance().addPropertiesWriter(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler());
		TGPropertiesManager.getInstance().addPropertiesReader(TGConfigDefaults.RESOURCE, new TGConfigDefaultsPropertiesHandler());
		TGPropertiesManager.getInstance().addPropertiesReader(TGPluginInfo.RESOURCE, new TGPluginInfoHandler());
		TGPropertiesManager.getInstance().addPropertiesReader(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler());
		TGPropertiesManager.getInstance().addPropertiesWriter(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler());
	}
}
