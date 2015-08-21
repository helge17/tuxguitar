package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class LilypondPluginSettingsHandler extends TGSongStreamSettingsHandlerPlugin {

	public String getModuleId() {
		return LilypondPluginExporter.MODULE_ID;
	}
	
	public TGSongStreamSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new LilypondSettingsHandler();
	}	
}
