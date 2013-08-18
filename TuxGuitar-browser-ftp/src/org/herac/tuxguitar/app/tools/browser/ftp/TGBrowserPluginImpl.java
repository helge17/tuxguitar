package org.herac.tuxguitar.app.tools.browser.ftp;

import org.herac.tuxguitar.app.system.plugins.base.TGBrowserPlugin;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;

public class TGBrowserPluginImpl extends TGBrowserPlugin {
	
	public static final String MODULE_ID = "tuxguitar-browser-ftp";
	
	protected TGBrowserFactory getFactory() {
		return new TGBrowserFactoryImpl();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
