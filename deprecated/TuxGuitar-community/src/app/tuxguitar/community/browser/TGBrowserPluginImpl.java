package app.tuxguitar.community.browser;

import app.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import app.tuxguitar.app.tools.browser.plugin.TGBrowserPlugin;
import app.tuxguitar.community.TGCommunityPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGBrowserPluginImpl extends TGBrowserPlugin {

	protected TGBrowserFactory getFactory(TGContext context) throws TGPluginException {
		return new TGBrowserFactoryImpl(context);
	}

	public String getModuleId(){
		return TGCommunityPlugin.MODULE_ID;
	}
}
