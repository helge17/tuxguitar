package org.herac.tuxguitar.app.system.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.herac.tuxguitar.util.properties.TGProperties;

public class TGPropertiesImpl implements TGProperties{
	
	private Properties properties;
	
	public TGPropertiesImpl(){
		this.properties = new Properties();
	}

	public String getValue(String key) {
		return this.properties.getProperty(key);
	}

	public void setValue(String key, String value) {
		this.properties.setProperty(key, value);
	}
	
	public void remove(String key) {
		this.properties.remove(key);
	}
	
	public void clear() {
		this.properties.clear();
	}
	
	public void load(InputStream inputStream) throws IOException {
		this.properties.load(inputStream);
	}
	
	public void store(OutputStream outputStream, String comments) throws IOException{
		this.properties.store(outputStream, comments);
	}
}
