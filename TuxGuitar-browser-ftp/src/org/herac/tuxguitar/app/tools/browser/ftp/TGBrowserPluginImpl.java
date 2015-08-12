package org.herac.tuxguitar.app.tools.browser.ftp;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.tools.browser.plugin.TGBrowserPlugin;

public class TGBrowserPluginImpl extends TGBrowserPlugin {
	
	public static final String MODULE_ID = "tuxguitar-browser-ftp";
	
	protected TGBrowserFactory getFactory() {
		return new TGBrowserFactoryImpl(getContext());
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
