package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.app.system.plugins.TGPluginException;
import org.herac.tuxguitar.app.system.plugins.base.TGBrowserPlugin;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;

public class TGBrowserPluginImpl extends TGBrowserPlugin {
	
	protected TGBrowserFactory getFactory() throws TGPluginException {
		return new TGBrowserFactoryImpl();
	}
	
}
