package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class SVGStylesHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public String getModuleId() {
		return SVGSongWriterPlugin.MODULE_ID;
	}
	
	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new SVGStylesHandler(context);
	}	
}
