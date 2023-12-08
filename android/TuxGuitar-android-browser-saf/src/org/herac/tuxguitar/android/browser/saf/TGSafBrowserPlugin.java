package org.herac.tuxguitar.android.browser.saf;

import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.plugin.TGBrowserPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGSafBrowserPlugin extends TGBrowserPlugin {

	public static final String MODULE_ID = "tuxguitar-android-browser-saf";

	public TGSafBrowserPlugin() {
		super();
	}

	public void connect(TGContext context) throws TGPluginException {
		if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ) {
			super.connect(context);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ) {
			super.disconnect(context);
		}
	}

	public TGBrowserFactory getFactory(TGContext context) {
		return new TGSafBrowserFactory(context);
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
