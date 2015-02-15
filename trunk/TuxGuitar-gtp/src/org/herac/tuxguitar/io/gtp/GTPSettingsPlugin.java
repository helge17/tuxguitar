package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GTPSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler getHandler() throws TGPluginException {
		return new GTPSettingsHandler(getContext());
	}
	
	public String getModuleId() {
		return GTPPlugin.MODULE_ID;
	}
}
