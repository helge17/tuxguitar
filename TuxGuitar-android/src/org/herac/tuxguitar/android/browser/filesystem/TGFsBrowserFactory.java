package org.herac.tuxguitar.android.browser.filesystem;

import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;

public class TGFsBrowserFactory implements TGBrowserFactory{
	
	public static final String BROWSER_TYPE = "file.system";
	public static final String BROWSER_NAME = "File System";
	
	private TGContext context;
	private TGFsBrowserSettingsFactory settingsFactory;
	
	public TGFsBrowserFactory(TGContext context, TGFsBrowserSettingsFactory settingsFactory) {
		this.context = context;
		this.settingsFactory = settingsFactory;
	}
	
	public String getType(){
		return BROWSER_TYPE;
	}
	
	public String getName(){
		return BROWSER_NAME;
	}
	
	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings) throws TGBrowserException {
		handler.onCreateBrowser(new TGFsBrowser(this.context, TGFsBrowserSettings.createInstance(settings)));
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) throws TGBrowserException {
		this.settingsFactory.createSettings(handler);
	}
}
