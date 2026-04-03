package app.tuxguitar.app.system.config;

import app.tuxguitar.app.system.properties.TGResourcePropertiesReader;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesException;
import app.tuxguitar.util.properties.TGPropertiesReader;

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
