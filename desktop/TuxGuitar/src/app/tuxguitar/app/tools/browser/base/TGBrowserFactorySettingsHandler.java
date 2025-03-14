package app.tuxguitar.app.tools.browser.base;

import app.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactorySettingsHandler extends TGErrorHandler {

	void onCreateSettings(TGBrowserSettings settings);
}
