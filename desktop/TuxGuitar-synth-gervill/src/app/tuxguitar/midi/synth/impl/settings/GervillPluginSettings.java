package app.tuxguitar.midi.synth.impl.settings;

import app.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.midi.synth.impl.GervillExtensionPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class GervillPluginSettings extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new GervillPluginSettingsHandler(context);
	}

	public String getModuleId() {
		return GervillExtensionPlugin.MODULE_ID;
	}
}
