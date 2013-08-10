package org.herac.tuxguitar.app.system.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

public class TGFilePropertiesWriter extends TGPropertiesBaseHandler implements TGPropertiesWriter{
	
	public TGFilePropertiesWriter(String prefix, String suffix) {
		super(prefix, suffix);
	}
	
	public void writeProperties(TGProperties properties, String module) throws TGPropertiesException {
		try {
			File file = new File(getPrefix() + module + getSuffix());
			if(!file.exists()){
				File folder = file.getParentFile();
				if(folder != null && !folder.exists()){
					folder.mkdirs();
				}
			}
			((TGPropertiesImpl)properties).store(new FileOutputStream(file), module);
		} catch (FileNotFoundException e) {
			throw new TGPropertiesException(e);
		} catch (IOException e) {
			throw new TGPropertiesException(e);
		}
	}
}
