package org.herac.tuxguitar.app.tools.browser.base.handler;

import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;

public interface TGBrowserListElementsHandler extends TGBrowserErrorHandler {
	
	void onSuccess(List<TGBrowserElement> elements);
	
}
