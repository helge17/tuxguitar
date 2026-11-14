package app.tuxguitar.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import app.tuxguitar.util.properties.TGProperties;

/**
 * Properties kept in a map: what the app's settings are made of, for the synthesizer's settings headless (none are
 * read, so Gervill uses its emergency soundbank).
 */
public final class MapProperties implements TGProperties {

	private final Map<String, String> values = new HashMap<String, String>();

	public String getValue(String key) {
		return this.values.get(key);
	}

	public void setValue(String key, String value) {
		this.values.put(key, value);
	}

	public void remove(String key) {
		this.values.remove(key);
	}

	public void clear() {
		this.values.clear();
	}

	public void update(Properties properties) {
	}

	public Set<String> getStringKeys() {
		return this.values.keySet();
	}
}
