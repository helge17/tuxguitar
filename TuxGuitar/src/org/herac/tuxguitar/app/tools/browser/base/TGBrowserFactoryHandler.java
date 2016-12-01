package org.herac.tuxguitar.app.tools.browser.base;

import org.herac.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactoryHandler extends TGErrorHandler {
	
	void onCreateBrowser(TGBrowser browser);
}
