package org.herac.tuxguitar.app.system.config;

import org.herac.tuxguitar.app.system.properties.TGResourcePropertiesReader;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;

public class TGConfigDefaultsPropertiesHandler implements TGPropertiesReader{
	
	private static final String DEFAULT_MODULE = "tuxguitar";
	private static final String DEFAULT_RESOURCE_PREFIX = "";
	private static final String DEFAULT_RESOURCE_SUFFIX = ".cfg";
	
	private TGContext context;
	
	public TGConfigDefaultsPropertiesHandler(TGContext context) {
		this.context = context;
	}
	
	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		this.readDefaultProperties(properties, module);
	}
	
	private void readDefaultProperties(TGProperties properties, String module) {
		if( DEFAULT_MODULE.equals(module) ){
			TGConfigDefaults.loadProperties(properties);
		}
		TGPropertiesReader tgPropertiesReader = new TGResourcePropertiesReader(this.context, DEFAULT_RESOURCE_PREFIX, DEFAULT_RESOURCE_SUFFIX);
		tgPropertiesReader.readProperties(properties, module);
	}
}
