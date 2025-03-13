package app.tuxguitar.midiinput;

import app.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class MidiSettingsPlugin extends TGPluginSettingsAdapter {

	public void connect(TGContext context) {
		MiConfig.init(context);

		super.connect(context);
	}

	protected TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new MiSettingsHandler();
	}

	public String getModuleId() {
		return MiPlugin.MODULE_ID;
	}
}
