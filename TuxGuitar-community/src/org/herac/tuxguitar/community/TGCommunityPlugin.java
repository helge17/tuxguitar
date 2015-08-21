package org.herac.tuxguitar.community;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGCommunityPlugin implements TGPlugin {

	public static final String MODULE_ID = "tuxguitar-community";
	
	public String getModuleId() {
		return MODULE_ID;
	}
	
	public void connect(TGContext context) throws TGPluginException {
		TGCommunitySingleton.getInstance(context).loadSettings();
	}

	public void disconnect(TGContext context) throws TGPluginException {
		TGCommunitySingleton.getInstance(context).saveSettings();
	}
}
