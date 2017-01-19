package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class GTPFileFormatVersion {
	
	private TGFileFormat fileFormat;
	private String version;
	private int versionCode;
	
	public GTPFileFormatVersion(TGFileFormat fileFormat, String version, int versionCode) {
		this.fileFormat = fileFormat;
		this.version = version;
		this.versionCode = versionCode;
	}

	public TGFileFormat getFileFormat() {
		return this.fileFormat;
	}

	public String getVersion() {
		return this.version;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
}
