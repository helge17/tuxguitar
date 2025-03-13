package app.tuxguitar.io.midi;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.plugin.TGSongReaderPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class MidiSongReaderPlugin extends TGSongReaderPlugin {

	public MidiSongReaderPlugin() {
		super(false);
	}

	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}

	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new MidiSongReader(context);
	}

	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return null;
	}
}
