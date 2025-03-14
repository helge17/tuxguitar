package app.tuxguitar.io.midi;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;

public class MidiSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) {
		return new MidiSettingsHandler(context);
	}

	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
