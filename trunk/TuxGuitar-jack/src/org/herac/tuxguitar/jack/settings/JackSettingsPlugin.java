package org.herac.tuxguitar.jack.settings;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.singleton.JackSettingsInstanceProvider;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class JackSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler getHandler() throws TGPluginException {
		return new JackSettingsHandler(new JackSettingsInstanceProvider());
	}
	
	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
