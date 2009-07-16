package org.herac.tuxguitar.gui.tools.custom.converter;

public class TGConverterFormat {
	
	private String extension;
	private Object exporter;
	
	public TGConverterFormat(String extension, Object exporter){
		this.extension = extension;
		this.exporter = exporter;
	}
	
	public String getExtension() {
		return this.extension;
	}
	
	public Object getExporter() {
		return this.exporter;
	}
}
