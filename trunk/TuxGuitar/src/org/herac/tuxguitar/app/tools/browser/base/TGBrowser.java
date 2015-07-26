package org.herac.tuxguitar.app.tools.browser.base;

import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdElementHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdRootHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdUpHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCloseHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserListElementsHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserOpenHandler;

public interface TGBrowser {
	
	void open(TGBrowserOpenHandler handler);
	
	void close(TGBrowserCloseHandler handler);
	
	void cdRoot(TGBrowserCdRootHandler handler);
	
	void cdUp(TGBrowserCdUpHandler handler);
	
	void cdElement(TGBrowserElement element, TGBrowserCdElementHandler handler);
	
	void listElements(TGBrowserListElementsHandler handler);
}
