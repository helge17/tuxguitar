package app.tuxguitar.io.image;

import app.tuxguitar.app.io.stream.TGSongStreamSettingsHandler;
import app.tuxguitar.app.io.stream.TGSongStreamSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;

public class ImageExporterSettingsPlugin extends TGSongStreamSettingsHandlerPlugin {

	public TGSongStreamSettingsHandler createSettingsHandler(TGContext context) {
		return new ImageExporterSettingsHandler(context);
	}

	public String getModuleId(){
		return ImageExporterPlugin.MODULE_ID;
	}
}
