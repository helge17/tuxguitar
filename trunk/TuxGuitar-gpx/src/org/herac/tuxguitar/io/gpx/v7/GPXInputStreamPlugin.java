package org.herac.tuxguitar.io.gpx.v7;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.gpx.GPXPlugin;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;

public class GPXInputStreamPlugin extends TGSongReaderPlugin{
	
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
		return GPXPlugin.MODULE_ID;
	}
}
