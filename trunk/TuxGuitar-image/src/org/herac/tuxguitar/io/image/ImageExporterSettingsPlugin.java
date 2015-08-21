package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;

public class ImageExporterSettingsPlugin extends TGSongStreamSettingsHandlerPlugin {
	
	public TGSongStreamSettingsHandler createSettingsHandler(TGContext context) {
		return new ImageExporterSettingsHandler(context);
	}
	
	public String getModuleId(){
		return ImageExporterPlugin.MODULE_ID;
	}
}
