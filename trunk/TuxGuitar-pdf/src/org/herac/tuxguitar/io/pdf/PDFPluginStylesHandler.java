package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class PDFPluginStylesHandler extends TGSongStreamSettingsHandlerPlugin {

	public String getModuleId() {
		return PDFPluginExporter.MODULE_ID;
	}
	
	public TGSongStreamSettingsHandler getSettingsHandler() throws TGPluginException {
		return new PDFStylesHandler();
	}	
}
