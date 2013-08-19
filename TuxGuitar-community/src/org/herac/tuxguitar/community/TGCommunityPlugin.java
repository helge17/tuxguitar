package org.herac.tuxguitar.community;

import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGCommunityPlugin implements TGPlugin {

	public static final String MODULE_ID = "tuxguitar-community";
	
	public void init() throws TGPluginException{
		TGCommunitySingleton.getInstance().loadSettings();
	}
	
	public void close() throws TGPluginException{
		TGCommunitySingleton.getInstance().saveSettings();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}

	public void setEnabled(boolean enabled) throws TGPluginException {
		// Not implemented.
	}
}
