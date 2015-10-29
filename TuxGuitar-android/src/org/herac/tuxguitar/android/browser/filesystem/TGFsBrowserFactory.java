package org.herac.tuxguitar.android.browser.filesystem;

import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;

public class TGFsBrowserFactory implements TGBrowserFactory{
	
	public static final String BROWSER_TYPE = "file.system";
	public static final String BROWSER_NAME = "File System";
	
	private TGFsBrowserSettingsFactory settingsFactory;
	
	public TGFsBrowserFactory(TGFsBrowserSettingsFactory settingsFactory) {
		this.settingsFactory = settingsFactory;
	}
	
	public String getType(){
		return BROWSER_TYPE;
	}
	
	public String getName(){
		return BROWSER_NAME;
	}
	
	public TGBrowserSettings restoreSettings(String string) {
		return TGFsBrowserSettings.fromString(string);
	}
	
	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings data) throws TGBrowserException {
		if( data instanceof TGFsBrowserSettings ){
			handler.onCreateBrowser(new TGFsBrowser((TGFsBrowserSettings)data));
		}
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) throws TGBrowserException {
		this.settingsFactory.createSettings(handler);
	}
}
