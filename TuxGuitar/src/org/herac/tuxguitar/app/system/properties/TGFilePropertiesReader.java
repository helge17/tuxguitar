package org.herac.tuxguitar.app.system.properties;

import java.io.File;
import java.io.FileInputStream;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;

public class TGFilePropertiesReader extends TGPropertiesBaseHandler implements TGPropertiesReader{
	
	public TGFilePropertiesReader(String prefix, String suffix) {
		super(prefix, suffix);
	}

	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		try {
			File file = new File(getPrefix() + module + getSuffix());
			if( file.exists() ){
				((TGPropertiesImpl)properties).clear();
				((TGPropertiesImpl)properties).load(new FileInputStream(file));
			}else{
				TGFilePropertiesWriter tgFilePropertiesWriter = new TGFilePropertiesWriter(getPrefix(), getSuffix());
				tgFilePropertiesWriter.writeProperties(properties, module);
			}
		} catch (Exception e) {
			throw new TGPropertiesException(e);
		}
	}
}
