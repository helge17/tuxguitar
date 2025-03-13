package org.herac.tuxguitar.app.system.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
		this.properties.load(new InputStreamReader(inputStream,StandardCharsets.UTF_8));
	}

	public void store(OutputStream outputStream, String comments) throws IOException{
		this.properties.store(new OutputStreamWriter(outputStream,StandardCharsets.UTF_8), comments);
	}

	public void update(Properties newProperties) {
		this.properties.putAll(newProperties);
	}

	public Set<String> getStringKeys() {
		HashSet<String> stringKeySet = new HashSet<String>();
		for (Object key : this.properties.keySet()) {
			if (key instanceof String) {
				stringKeySet.add((String)key);
			}
		}
		return stringKeySet;
	}

}
