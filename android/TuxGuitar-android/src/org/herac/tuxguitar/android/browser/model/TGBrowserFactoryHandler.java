package org.herac.tuxguitar.android.browser.model;

import org.herac.tuxguitar.util.error.TGErrorHandler;

public interface TGBrowserFactoryHandler extends TGErrorHandler {
	
	void onCreateBrowser(TGBrowser browser);
}
