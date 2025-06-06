package app.tuxguitar.io.gpx.v6;

import java.io.InputStream;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatDetector;

public class GPXFileFormatDetector implements TGFileFormatDetector {

	public GPXFileFormatDetector() {
		super();
	}

	public TGFileFormat getFileFormat(InputStream is) {
		try {
			GPXFileSystem gpxFileSystem = new GPXFileSystem();
			if( gpxFileSystem.isSupportedHeader(gpxFileSystem.getHeader(is))) {
				return GPXInputStream.FILE_FORMAT;
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}
}
