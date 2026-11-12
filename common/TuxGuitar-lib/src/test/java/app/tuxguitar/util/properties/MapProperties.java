package app.tuxguitar.util.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Properties kept in a map, which tests read and fill directly.
 */
public class MapProperties implements TGProperties {

	public final Map<String, String> values = new HashMap<String, String>();

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

	public void update(Properties newProperties) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getStringKeys() {
		return this.values.keySet();
	}
}
