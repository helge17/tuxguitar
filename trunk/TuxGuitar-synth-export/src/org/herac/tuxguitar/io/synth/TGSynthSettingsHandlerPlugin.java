package org.herac.tuxguitar.io.synth;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;

public class TGSynthSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {
	
	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) {
		return new TGSynthSettingsHandler(context);
	}
	
	public String getModuleId(){
		return TGSynthSongWriterPlugin.MODULE_ID;
	}
}
