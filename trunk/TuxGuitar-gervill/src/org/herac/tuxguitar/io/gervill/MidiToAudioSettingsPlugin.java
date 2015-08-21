package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiToAudioSettingsPlugin extends TGSongStreamSettingsHandlerPlugin {
	
	public TGSongStreamSettingsHandler createSettingsHandler(TGContext context) {
		return new MidiToAudioSettingsHandler(context);
	}
	
	public String getModuleId(){
		return MidiToAudioPlugin.MODULE_ID;
	}
}
