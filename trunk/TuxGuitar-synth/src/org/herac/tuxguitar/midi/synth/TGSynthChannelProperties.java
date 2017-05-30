package org.herac.tuxguitar.midi.synth;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.util.properties.TGProperties;

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
}
