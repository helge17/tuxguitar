package org.herac.tuxguitar.io.gpx.v7;

import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;

public class GPXFileFormatDetector implements TGFileFormatDetector {
	
	public GPXFileFormatDetector() {
		super();
	}
	
	public TGFileFormat getFileFormat(InputStream is) {		
		try {
			GPXFileSystem gpxFileSystem = new GPXFileSystem();
			gpxFileSystem.load(is);
			if( gpxFileSystem.isSupportedVersion()) {
				return GPXInputStream.FILE_FORMAT;
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}
}
