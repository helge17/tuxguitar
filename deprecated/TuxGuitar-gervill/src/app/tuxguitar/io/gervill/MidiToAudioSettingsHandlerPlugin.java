package app.tuxguitar.io.gervill;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;

public class MidiToAudioSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) {
		return new MidiToAudioSettingsHandler(context);
	}

	public String getModuleId(){
		return MidiToAudioSongWriterPlugin.MODULE_ID;
	}
}
