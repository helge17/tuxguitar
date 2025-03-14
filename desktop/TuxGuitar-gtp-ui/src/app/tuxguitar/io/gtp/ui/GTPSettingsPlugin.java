package app.tuxguitar.io.gtp.ui;

import app.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.io.gtp.GTPPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class GTPSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new GTPSettingsHandler(context);
	}

	public String getModuleId() {
		return GTPPlugin.MODULE_ID;
	}
}
