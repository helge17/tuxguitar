package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGBrowserPlugin;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;

public class TGBrowserPluginImpl extends TGBrowserPlugin {
	
	protected TGBrowserFactory getFactory() throws TGPluginException {
		return new TGBrowserFactoryImpl();
	}
	
}
