package app.tuxguitar.io.gpx.v7;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.gpx.GPXPlugin;
import app.tuxguitar.io.plugin.TGSongReaderPlugin;
import app.tuxguitar.util.TGContext;

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
