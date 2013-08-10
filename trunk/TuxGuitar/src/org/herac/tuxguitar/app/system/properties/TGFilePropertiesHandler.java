package org.herac.tuxguitar.app.system.properties;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

public class TGFilePropertiesHandler extends TGPropertiesBaseHandler implements TGPropertiesReader, TGPropertiesWriter{
	
	public TGFilePropertiesHandler(String prefix, String suffix) {
		super(prefix, suffix);
	}

	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		TGFilePropertiesReader tgFilePropertiesReader = new TGFilePropertiesReader(getPrefix(), getSuffix());
		tgFilePropertiesReader.readProperties(properties, module);
	}

	public void writeProperties(TGProperties properties, String module) throws TGPropertiesException {
		TGFilePropertiesWriter tgFilePropertiesWriter = new TGFilePropertiesWriter(getPrefix(), getSuffix());
		tgFilePropertiesWriter.writeProperties(properties, module);
	}
}
