package app.tuxguitar.io.ptb;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.plugin.TGSongReaderPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class PTInputStreamPlugin extends TGSongReaderPlugin{

	public static final String MODULE_ID = "tuxguitar-ptb";

	public PTInputStreamPlugin() {
		super(true);
	}

	protected TGSongReader createInputStream(TGContext context) {
		return new PTInputStream();
	}

	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new PTFileFormatDetector();
	}

	public String getModuleId(){
		return MODULE_ID;
	}
}
