package org.herac.tuxguitar.app.tools.browser.ftp;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.tools.browser.plugin.TGBrowserPlugin;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserPluginImpl extends TGBrowserPlugin {
	
	public static final String MODULE_ID = "tuxguitar-browser-ftp";
	
	protected TGBrowserFactory getFactory(TGContext context) {
		return new TGBrowserFactoryImpl(context);
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
