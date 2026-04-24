package app.tuxguitar.android.browser.saf;

import app.tuxguitar.android.browser.model.TGBrowserFactory;
import app.tuxguitar.android.browser.plugin.TGBrowserPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGSafBrowserPlugin extends TGBrowserPlugin {

	public static final String MODULE_ID = "tuxguitar-android-browser-saf";

	public TGSafBrowserPlugin() {
		super();
	}

	public void connect(TGContext context) throws TGPluginException {
		super.connect(context);
	}

	public void disconnect(TGContext context) throws TGPluginException {
		super.disconnect(context);
	}

	public TGBrowserFactory getFactory(TGContext context) {
		return new TGSafBrowserFactory(context);
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
