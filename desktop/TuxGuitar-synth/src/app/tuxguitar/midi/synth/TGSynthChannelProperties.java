package app.tuxguitar.midi.synth;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import app.tuxguitar.util.properties.TGProperties;

public class TGSynthChannelProperties implements TGProperties{

	private Map<String, String> properties;

	public TGSynthChannelProperties() {
		this.properties = new HashMap<String, String>();
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public String getValue(String key) {
		return this.properties.get(key);
	}

	public void setValue(String key, String value) {
		this.properties.put(key, value);
	}

	public void remove(String key) {
		this.properties.remove(key);
	}

	public void clear() {
		this.properties.clear();
	}

	public void update(Properties newProperties) {
		HashMap<String, String> newPropertiesMap = new HashMap<>();
		for (Map.Entry<Object, Object> entry : newProperties.entrySet()) {
			newPropertiesMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
			}
		this.properties.putAll((Map<String, String>)newPropertiesMap);
	}

	public Set<String> getStringKeys() {
		return this.properties.keySet();
	}

}
