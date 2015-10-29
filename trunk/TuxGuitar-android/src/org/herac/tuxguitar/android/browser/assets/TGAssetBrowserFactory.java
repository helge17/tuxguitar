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
	
	public TGBrowserSettings restoreSettings(String string) {
		if( this.getDefaultSettings().getId().equals(string)) {
			return this.getDefaultSettings();
		}
		return null;
	}
	
	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings data) throws TGBrowserException {
		if( data instanceof TGAssetBrowserSettings ){
			handler.onCreateBrowser(new TGAssetBrowser(this.context, this.getDefaultSettings()));
		}
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) throws TGBrowserException {
		if( TGBrowserManager.getInstance(this.context).getCollection(this.getType(), this.getDefaultSettings()) != null ) {
			throw new TGBrowserException(BROWSER_NAME + " already exists.");
		}
		
		handler.onCreateSettings(new TGAssetBrowserSettings());
	}
}
