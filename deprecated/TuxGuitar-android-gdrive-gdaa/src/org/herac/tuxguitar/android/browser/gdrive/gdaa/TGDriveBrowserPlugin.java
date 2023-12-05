package org.herac.tuxguitar.android.browser.gdrive.gdaa;

import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.plugin.TGBrowserPlugin;
import org.herac.tuxguitar.util.TGContext;

public class TGDriveBrowserPlugin extends TGBrowserPlugin {

	public static final String MODULE_ID = "tuxguitar-android-gdrive";
	
	protected TGBrowserFactory getFactory(TGContext context) {
		return new TGDriveBrowserFactory(context);
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
