package org.herac.tuxguitar.io.tg;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class TGFileFormatVersion {
	
	private TGFileFormat fileFormat;
	private String version;
	
	public TGFileFormatVersion(TGFileFormat fileFormat, String version) {
		this.fileFormat = fileFormat;
		this.version = version;
	}

	public TGFileFormat getFileFormat() {
		return this.fileFormat;
	}

	public String getVersion() {
		return this.version;
	}
}
