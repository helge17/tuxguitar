package org.herac.tuxguitar.app.tools.browser.base;

import org.herac.tuxguitar.ui.widget.UIWindow;

public interface TGBrowserFactory {
	
	String getName();
	
	String getType();
	
	TGBrowserSettings dataDialog(UIWindow parent);
	
	TGBrowser newTGBrowser(TGBrowserSettings data);
}
