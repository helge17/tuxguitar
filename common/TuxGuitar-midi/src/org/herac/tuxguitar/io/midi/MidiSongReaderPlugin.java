package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiSongReaderPlugin extends TGSongReaderPlugin {
	
	public MidiSongReaderPlugin() {
		super(false);
	}

	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}

	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new MidiSongReader();
	}

	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return null;
	}
}
