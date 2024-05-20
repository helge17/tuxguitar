package org.herac.tuxguitar.util.properties;

import java.util.Properties;
import java.util.Set;

public interface TGProperties {
	
	public String getValue(String key);
	
	public void setValue(String key, String value);
	
	public void remove(String key);
	
	public void clear();
	
	public void update(Properties newProperties);
	
	public Set<String> getStringKeys();
	
}
