package app.tuxguitar.io.tef3;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.plugin.TGSongReaderPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TESongReaderPlugin extends TGSongReaderPlugin {

	public static final String MODULE_ID = "tuxguitar-tef3";

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
