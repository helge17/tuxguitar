package app.tuxguitar.tools.browser.base;

import app.tuxguitar.tools.browser.base.TGBrowserSettings;
import app.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactorySettingsHandler extends TGErrorHandler {

	void onCreateSettings(TGBrowserSettings settings);
}
