package app.tuxguitar.app.tools.browser.ftp;

import app.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import app.tuxguitar.app.tools.browser.plugin.TGBrowserPlugin;
import app.tuxguitar.util.TGContext;

public class TGBrowserPluginImpl extends TGBrowserPlugin {

	public static final String MODULE_ID = "tuxguitar-browser-ftp";

	protected TGBrowserFactory getFactory(TGContext context) {
		return new TGBrowserFactoryImpl(context);
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
