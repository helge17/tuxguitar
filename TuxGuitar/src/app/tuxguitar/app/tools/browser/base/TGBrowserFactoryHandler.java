package app.tuxguitar.app.tools.browser.base;

import app.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactoryHandler extends TGErrorHandler {

	void onCreateBrowser(TGBrowser browser);
}
