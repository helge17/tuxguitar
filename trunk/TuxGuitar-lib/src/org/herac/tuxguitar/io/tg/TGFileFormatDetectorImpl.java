package org.herac.tuxguitar.io.tg;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;

public class TGFileFormatDetectorImpl implements TGFileFormatDetector {
	
	private TGFileFormatVersion[] supportedVersions;
	
	public TGFileFormatDetectorImpl(TGFileFormatVersion... supportedVersions) {
		this.supportedVersions = supportedVersions;
	}
	
	public TGFileFormat getFileFormat(InputStream is) {
		try {
			String version = this.readVersion(new DataInputStream(is));
			if( version != null ) {
				for(TGFileFormatVersion supportedVersion : this.supportedVersions) {
					if( version.equals(supportedVersion.getVersion())) {
						return supportedVersion.getFileFormat();
					}
				}
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}
	
	private String readVersion(DataInputStream is) throws IOException {
		int length = (is.read() & 0xFF);
		
		char[] chars = new char[length];
		for(int i = 0;i < chars.length; i++){
			chars[i] = is.readChar();
		}
		return String.copyValueOf(chars);
	}
}
