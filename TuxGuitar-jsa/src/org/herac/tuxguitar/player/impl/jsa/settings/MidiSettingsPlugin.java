package org.herac.tuxguitar.player.impl.jsa.settings;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.player.impl.jsa.MidiPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler getHandler() throws TGPluginException {
		return new MidiSettingsHandler();
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
