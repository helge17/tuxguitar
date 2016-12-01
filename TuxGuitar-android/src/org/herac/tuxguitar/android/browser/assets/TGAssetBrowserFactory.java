package org.herac.tuxguitar.android.browser.assets;

import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;

public class TGAssetBrowserFactory implements TGBrowserFactory{
	
	public static final String BROWSER_TYPE = "assets";
	public static final String BROWSER_NAME = "Demo Songs";
	
	private TGContext context;
	private TGAssetBrowserSettings settings;
	
	public TGAssetBrowserFactory(TGContext context) {
		this.context = context;
		this.settings = new TGAssetBrowserSettings();
	}
	
	public String getType(){
		return BROWSER_TYPE;
	}
	
	public String getName(){
		return BROWSER_NAME;
	}
	
	public TGAssetBrowserSettings getDefaultSettings() {
		return this.settings;
	}
	
	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings data) {
		handler.onCreateBrowser(new TGAssetBrowser(this.context, this.getDefaultSettings()));
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) {
		try {
			TGBrowserSettings settings = this.getDefaultSettings().toBrowserSettings();
			if (TGBrowserManager.getInstance(this.context).getCollection(this.getType(), settings) != null) {
				throw new TGBrowserException(BROWSER_NAME + " already exists.");
			}

			handler.onCreateSettings(settings);
		} catch(TGBrowserException e) {
			handler.handleError(e);
		}
	}
}
