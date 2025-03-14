package app.tuxguitar.android.browser.model;

import app.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactorySettingsHandler extends TGErrorHandler {

	void onCreateSettings(TGBrowserSettings settings);
}
