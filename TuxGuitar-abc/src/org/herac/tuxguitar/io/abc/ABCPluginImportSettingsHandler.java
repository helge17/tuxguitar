package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandlerPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class ABCPluginImportSettingsHandler extends TGSongStreamSettingsHandlerPlugin {

	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
	
	public TGSongStreamSettingsHandler getSettingsHandler() throws TGPluginException {
		return new ABCImportSettingsHandler(this.getContext());
	}	
}
