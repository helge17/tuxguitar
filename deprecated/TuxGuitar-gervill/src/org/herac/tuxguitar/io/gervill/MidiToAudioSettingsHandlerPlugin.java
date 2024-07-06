package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiToAudioSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {
	
	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) {
		return new MidiToAudioSettingsHandler(context);
	}
	
	public String getModuleId(){
		return MidiToAudioSongWriterPlugin.MODULE_ID;
	}
}
