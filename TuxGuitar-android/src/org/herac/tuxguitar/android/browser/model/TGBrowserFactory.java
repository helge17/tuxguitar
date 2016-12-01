package org.herac.tuxguitar.android.browser.model;

public interface TGBrowserFactory {
	
	String getName();
	
	String getType();
	
	void createSettings(TGBrowserFactorySettingsHandler handler);
	
	void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings);
	
}
