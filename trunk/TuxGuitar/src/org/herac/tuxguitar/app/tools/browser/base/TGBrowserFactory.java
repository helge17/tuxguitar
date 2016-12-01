package org.herac.tuxguitar.app.tools.browser.base;

public interface TGBrowserFactory {
	
	String getName();
	
	String getType();
	
	void createSettings(TGBrowserFactorySettingsHandler handler);
	
	void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings);
}
