package org.herac.tuxguitar.app.tools.custom.converter;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class TGConverterFormat {
	
	private TGFileFormat fileFormat;
	private String extension;
	
	public TGConverterFormat(TGFileFormat fileFormat, String extension){
		this.fileFormat = fileFormat;
		this.extension = extension;
	}
	
	public TGFileFormat getFileFormat() {
		return fileFormat;
	}

	public String getExtension() {
		return this.extension;
	}
}
