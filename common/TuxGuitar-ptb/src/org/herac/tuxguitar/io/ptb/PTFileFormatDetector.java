package org.herac.tuxguitar.io.ptb;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;

public class PTFileFormatDetector implements TGFileFormatDetector {
	
	public static final String PTB_VERSION = "ptab-4";
	
	public PTFileFormatDetector() {
		super();
	}
	
	public TGFileFormat getFileFormat(InputStream is) {
		try {
			String version = this.readVersion(is);
			if( PTB_VERSION.equals(version)) {
				return PTInputStream.FILE_FORMAT;
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}
	
	private String readVersion(InputStream is){
		try {
			byte[] ptabHeader = new byte[4];
			byte[] ptabVersion = {0, 0};
			
			is.read(ptabHeader);
			is.read(ptabVersion);
			
			String header = new String(ptabHeader);
			String version = String.valueOf(((ptabVersion[1] & 0xff) << 8) | (ptabVersion[0] & 0xff));
			
			return new String(header + "-" + version);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
