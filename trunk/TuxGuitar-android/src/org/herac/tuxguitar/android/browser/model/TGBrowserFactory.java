package org.herac.tuxguitar.android.browser.model;

public interface TGBrowserFactory {
	
	String getName();
	
	String getType();
	
	TGBrowserSettings restoreSettings(String settings);
	
	void createSettings(TGBrowserFactorySettingsHandler handler) throws TGBrowserException;
	
	void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings) throws TGBrowserException;
	
}
