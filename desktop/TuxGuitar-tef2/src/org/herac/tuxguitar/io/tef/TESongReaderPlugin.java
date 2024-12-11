package org.herac.tuxguitar.io.tef2;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TESongReaderPlugin extends TGSongReaderPlugin {
	
	public static final String MODULE_ID = "tuxguitar-tef2";
	
	public TESongReaderPlugin() {
		super(true);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
	
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new TESongReader();
	}
	
	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new TEFileFormatDetector();
	}
}
