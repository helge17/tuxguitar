package app.tuxguitar.io.synth;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;

public class TGSynthSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) {
		return new TGSynthSettingsHandler(context);
	}

	public String getModuleId(){
		return TGSynthSongWriterPlugin.MODULE_ID;
	}
}
