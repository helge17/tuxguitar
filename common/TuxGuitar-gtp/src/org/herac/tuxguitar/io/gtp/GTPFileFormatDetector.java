package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;

public class GTPFileFormatDetector implements TGFileFormatDetector {
	
	private GTPFileFormatVersion[] supportedVersions;
	
	public GTPFileFormatDetector(GTPFileFormatVersion... supportedVersions) {
		this.supportedVersions = supportedVersions;
	}
	
	public TGFileFormat getFileFormat(InputStream is) {
		GTPFileFormatVersion version = this.getFileFormatVersion(is);
		if( version != null ) {
			return version.getFileFormat();
		}
		return null;
	}
	
	public GTPFileFormatVersion getFileFormatVersion(InputStream is) {
		try {
			String version = this.readVersion(is);
			if( version != null ) {
				for(GTPFileFormatVersion supportedVersion : this.supportedVersions) {
					if( version.equals(supportedVersion.getVersion())) {
						return supportedVersion;
					}
				}
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}
	
	public String readVersion(InputStream is) throws IOException {
		int len = (is.read() & 0xff);
		int strLen = (len >= 0 && len <= 30 ? len : 30);
		
		byte[] bytes = new byte[30];
		
		is.read(bytes);
		
		return new String(new String(bytes, 0, strLen, GTPFileFormat.DEFAULT_VERSION_CHARSET).getBytes(GTPFileFormat.DEFAULT_TG_CHARSET), GTPFileFormat.DEFAULT_TG_CHARSET);
	}
}
