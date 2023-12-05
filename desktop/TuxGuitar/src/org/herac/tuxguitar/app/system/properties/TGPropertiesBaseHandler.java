package org.herac.tuxguitar.app.system.properties;

public class TGPropertiesBaseHandler {
	
	private String prefix;
	private String suffix;
	
	public TGPropertiesBaseHandler(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}	
}
