package org.herac.tuxguitar.util.properties;

public interface TGProperties {
	
	public String getValue(String key);
	
	public void setValue(String key, String value);
	
	public void remove(String key);
	
	public void clear();
	
}
