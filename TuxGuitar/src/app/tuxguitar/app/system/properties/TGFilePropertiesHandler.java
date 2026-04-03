package app.tuxguitar.app.system.properties;

import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesException;
import app.tuxguitar.util.properties.TGPropertiesReader;
import app.tuxguitar.util.properties.TGPropertiesWriter;

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
