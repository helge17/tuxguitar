package org.herac.tuxguitar.app.system.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

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
				Properties newProperties = new Properties();
				newProperties.load(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));
				properties.update(newProperties);
			}else{
				TGFilePropertiesWriter tgFilePropertiesWriter = new TGFilePropertiesWriter(getPrefix(), getSuffix());
				tgFilePropertiesWriter.writeProperties(properties, module);
			}
		} catch (Exception e) {
			throw new TGPropertiesException(e);
		}
	}
}
