package org.herac.tuxguitar.app.system.plugins;

import org.herac.tuxguitar.app.system.properties.TGResourcePropertiesReader;

public class TGPluginInfoHandler extends TGResourcePropertiesReader {
	
	private static final String RESOURCE_PREFIX = "META-INF/";
	private static final String RESOURCE_SUFFIX = ".info";
	
	public TGPluginInfoHandler() {
		super(RESOURCE_PREFIX, RESOURCE_SUFFIX);
	}
}
