package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class SVGExporterStylesPlugin extends TGSongStreamSettingsHandlerPlugin {

	public String getModuleId() {
		return SVGExporterPlugin.MODULE_ID;
	}
	
	public TGSongStreamSettingsHandler getSettingsHandler() throws TGPluginException {
		return new SVGExporterStylesHandler();
	}	
}
