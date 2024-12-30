package org.herac.tuxguitar.io.tef3;

import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;

public class TEFileFormatDetector implements TGFileFormatDetector {
	
	public TEFileFormatDetector() {
		super();
	}
	
	public TGFileFormat getFileFormat(InputStream is) {		
		try {
			TEInputStream tef3InputStream = new TEInputStream(is);
			boolean ok = tef3InputStream.readFileHeader();
			if (ok) {
				return TESongReader.FILE_FORMAT;
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}
}
