package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;

public class ImageExporterSettingsPlugin extends TGSongStreamSettingsHandlerPlugin {
	
	public TGSongStreamSettingsHandler getSettingsHandler() {
		return new ImageExporterSettingsHandler();
	}
	
	public String getModuleId(){
		return ImageExporterPlugin.MODULE_ID;
	}
}
