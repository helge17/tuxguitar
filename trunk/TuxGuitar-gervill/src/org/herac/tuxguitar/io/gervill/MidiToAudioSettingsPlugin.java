package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;

public class MidiToAudioSettingsPlugin extends TGSongStreamSettingsHandlerPlugin {
	
	public TGSongStreamSettingsHandler getSettingsHandler() {
		return new MidiToAudioSettingsHandler();
	}
	
	public String getModuleId(){
		return MidiToAudioPlugin.MODULE_ID;
	}
}
