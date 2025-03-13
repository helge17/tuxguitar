package app.tuxguitar.android.browser.model;

import app.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactoryHandler extends TGErrorHandler {

	void onCreateBrowser(TGBrowser browser);
}
