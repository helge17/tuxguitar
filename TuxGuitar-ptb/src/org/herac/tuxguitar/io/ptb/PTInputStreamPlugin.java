package org.herac.tuxguitar.io.ptb;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
