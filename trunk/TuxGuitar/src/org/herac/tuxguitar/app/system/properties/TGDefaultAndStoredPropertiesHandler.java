package org.herac.tuxguitar.app.system.properties;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

public class TGDefaultAndStoredPropertiesHandler implements TGPropertiesReader, TGPropertiesWriter{
	
	private TGContext context;
	private String defaultsPrefix;
	private String defaultsSuffix;
	private String storedPrefix;
	private String storedSuffix;
	
	public TGDefaultAndStoredPropertiesHandler(TGContext context, String defaultsPrefix, String defaultsSuffix, String storedPrefix, String storedSuffix) {
		this.context = context;
		this.defaultsPrefix = defaultsPrefix;
		this.defaultsSuffix = defaultsSuffix;
		this.storedPrefix = storedPrefix;
		this.storedSuffix = storedSuffix;
	}

	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		this.readDefaultProperties(properties, module);
		this.readStoredProperties(properties, module);
	}

	private void readDefaultProperties(TGProperties properties, String module) {
		TGPropertiesReader tgPropertiesReader = new TGResourcePropertiesReader(this.context, this.defaultsPrefix, this.defaultsSuffix);
		tgPropertiesReader.readProperties(properties, module);
	}
	
	private void readStoredProperties(TGProperties properties, String module) {
		TGPropertiesReader tgPropertiesReader = new TGFilePropertiesHandler(this.storedPrefix, this.storedSuffix);
		tgPropertiesReader.readProperties(properties, module);
	}
	
	public void writeProperties(TGProperties properties, String module) throws TGPropertiesException {
		TGPropertiesWriter tgPropertiesWriter = new TGFilePropertiesWriter(this.storedPrefix, this.storedSuffix);
		tgPropertiesWriter.writeProperties(properties, module);
	}
}
