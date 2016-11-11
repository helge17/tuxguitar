package org.herac.tuxguitar.io.base;

public class TGFileFormat {

	private String name;
	private String[] supportedFormats;
	
	public TGFileFormat(String name, String[] supportedFormats) {
		this.name = name;
		this.supportedFormats = supportedFormats;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String[] getSupportedFormats() {
		return this.supportedFormats;
	}
}
