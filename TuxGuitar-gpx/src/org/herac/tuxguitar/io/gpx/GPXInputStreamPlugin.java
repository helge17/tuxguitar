package org.herac.tuxguitar.io.gpx;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class GPXInputStreamPlugin extends TGSongReaderPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gpx";
	
	public GPXInputStreamPlugin() {
		super(true);
	}
	
	protected TGSongReader createInputStream(TGContext context) {
		return new GPXInputStream();
	}
	
	protected TGFileFormatDetector createFileFormatDetector(TGContext context) {
		return new GPXFileFormatDetector();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
