package app.tuxguitar.io.abc;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.plugin.TGSongReaderPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class ABCSongReaderPlugin extends TGSongReaderPlugin {

	public ABCSongReaderPlugin() {
		super(false);
	}

	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}

	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new ABCSongReader();
	}

	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return null;
	}
}
