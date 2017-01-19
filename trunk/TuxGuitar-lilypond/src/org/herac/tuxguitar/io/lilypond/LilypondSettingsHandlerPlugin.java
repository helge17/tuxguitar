package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class LilypondSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {
	
	public String getModuleId() {
		return LilypondSongWriterPlugin.MODULE_ID;
	}
	
	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new LilypondSettingsHandler(context);
	}	
}
