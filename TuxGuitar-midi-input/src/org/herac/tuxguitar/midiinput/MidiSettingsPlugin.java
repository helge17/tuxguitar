package org.herac.tuxguitar.midiinput;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiSettingsPlugin extends TGPluginSettingsAdapter {

	public void init(TGContext context) {
		MiConfig.init(context);
		
		super.init(context);
	}
	
	protected TGPluginSettingsHandler getHandler() throws TGPluginException {
		return new MiSettingsHandler();
	}
	
	public String getModuleId() {
		return MiPlugin.MODULE_ID;
	}
}
