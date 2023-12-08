package org.herac.tuxguitar.io.tef;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TESongReaderPlugin extends TGSongReaderPlugin {
	
	public static final String MODULE_ID = "tuxguitar-tef";
	
	public TESongReaderPlugin() {
		super(false);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
	
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new TESongReader();
	}
	
	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return null;
	}
}
