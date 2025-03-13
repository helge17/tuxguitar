package app.tuxguitar.io.lilypond;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class LilypondSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public String getModuleId() {
		return LilypondSongWriterPlugin.MODULE_ID;
	}

	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new LilypondSettingsHandler(context);
	}
}
