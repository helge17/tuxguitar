package app.tuxguitar.community;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

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
