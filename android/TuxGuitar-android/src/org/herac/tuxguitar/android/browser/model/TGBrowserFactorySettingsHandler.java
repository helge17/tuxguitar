package org.herac.tuxguitar.android.browser.model;

import org.herac.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactorySettingsHandler extends TGErrorHandler {
	
	void onCreateSettings(TGBrowserSettings settings);
}
