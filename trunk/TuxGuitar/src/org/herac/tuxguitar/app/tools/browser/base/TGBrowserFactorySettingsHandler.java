package org.herac.tuxguitar.app.tools.browser.base;

import org.herac.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactorySettingsHandler extends TGErrorHandler {
	
	void onCreateSettings(TGBrowserSettings settings);
}
