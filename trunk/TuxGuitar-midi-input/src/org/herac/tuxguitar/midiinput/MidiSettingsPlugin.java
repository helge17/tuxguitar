package org.herac.tuxguitar.midiinput;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler getHandler() throws TGPluginException {
		return new MiSettingsHandler();
	}
	
	public String getModuleId() {
		return MiPlugin.MODULE_ID;
	}
}
