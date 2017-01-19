package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {
	
	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) {
		return new MidiSettingsHandler(context);
	}
	
	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
