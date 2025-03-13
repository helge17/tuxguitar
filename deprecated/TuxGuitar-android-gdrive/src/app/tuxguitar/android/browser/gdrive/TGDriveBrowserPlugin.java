package app.tuxguitar.android.browser.gdrive;

import app.tuxguitar.android.browser.model.TGBrowserFactory;
import app.tuxguitar.android.browser.plugin.TGBrowserPlugin;
import app.tuxguitar.util.TGContext;

public class TGDriveBrowserPlugin extends TGBrowserPlugin {

	public static final String MODULE_ID = "tuxguitar-android-gdrive";

	protected TGBrowserFactory getFactory(TGContext context) {
		return new TGDriveBrowserFactory(context);
	}

	public String getModuleId() {
		return MODULE_ID;
	}
}
