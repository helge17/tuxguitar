package app.tuxguitar.player.impl.jsa.settings;

import app.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.player.impl.jsa.MidiPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class MidiSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new MidiSettingsHandler(context);
	}

	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
