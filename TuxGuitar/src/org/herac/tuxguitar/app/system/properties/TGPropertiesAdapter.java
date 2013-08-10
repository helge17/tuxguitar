package org.herac.tuxguitar.app.system.properties;

import org.herac.tuxguitar.app.system.config.TGConfigPropertiesHandler;
import org.herac.tuxguitar.app.system.plugins.TGPluginProperties;
import org.herac.tuxguitar.app.system.plugins.TGPluginPropertiesHandler;
import org.herac.tuxguitar.util.configuration.TGConfigManager;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;

public class TGPropertiesAdapter {
	
	public static void initialize() {
		TGPropertiesManager.getInstance().setPropertiesFactory(new TGPropertiesFactoryImpl());
		TGPropertiesManager.getInstance().addPropertiesReader(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler());
		TGPropertiesManager.getInstance().addPropertiesWriter(TGConfigManager.RESOURCE, new TGConfigPropertiesHandler());
		TGPropertiesManager.getInstance().addPropertiesReader(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler());
		TGPropertiesManager.getInstance().addPropertiesWriter(TGPluginProperties.RESOURCE, new TGPluginPropertiesHandler());
	}
}
