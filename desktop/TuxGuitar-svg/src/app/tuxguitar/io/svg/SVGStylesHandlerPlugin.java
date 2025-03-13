package app.tuxguitar.io.svg;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class SVGStylesHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public String getModuleId() {
		return SVGSongWriterPlugin.MODULE_ID;
	}

	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new SVGStylesHandler(context);
	}
}
