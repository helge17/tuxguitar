package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
