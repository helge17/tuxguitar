package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiPluginSettingsHandler extends TGSongStreamSettingsHandlerPlugin {
	
	public TGSongStreamSettingsHandler createSettingsHandler(TGContext context) {
		return new MidiSettingsHandler(context);
	}
	
	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
