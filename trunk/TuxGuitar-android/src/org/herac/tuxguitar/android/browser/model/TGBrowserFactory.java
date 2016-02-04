package org.herac.tuxguitar.android.browser.model;

public interface TGBrowserFactory {
	
	String getName();
	
	String getType();
	
	void createSettings(TGBrowserFactorySettingsHandler handler) throws TGBrowserException;
	
	void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings) throws TGBrowserException;
	
}
