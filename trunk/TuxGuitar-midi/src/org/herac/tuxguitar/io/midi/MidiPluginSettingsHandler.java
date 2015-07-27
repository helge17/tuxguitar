package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;

public class MidiPluginSettingsHandler extends TGSongStreamSettingsHandlerPlugin {
	
	public TGSongStreamSettingsHandler getSettingsHandler() {
		return new MidiSettingsHandler(this.getContext());
	}
	
	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
