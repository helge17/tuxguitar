package org.herac.tuxguitar.util.properties;

public interface TGPropertiesWriter {
	
	public void writeProperties(TGProperties properties, String module) throws TGPropertiesException;
	
}
