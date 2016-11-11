package org.herac.tuxguitar.android.browser.saf;

import android.net.Uri;

import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;

public class TGSafBrowserFactory implements TGBrowserFactory{

	public static final String BROWSER_TYPE = "saf";
	public static final String BROWSER_NAME = "Storage Folder";

	private TGContext context;

	public TGSafBrowserFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType(){
		return BROWSER_TYPE;
	}
	
	public String getName(){
		return BROWSER_NAME;
	}
	
	public void createBrowser(final TGBrowserFactoryHandler handler, final TGBrowserSettings settings) throws TGBrowserException {
		handler.onCreateBrowser(new TGSafBrowser(this.context, TGSafBrowserSettings.createInstance(settings)));
	}

	public void createSettings(final TGBrowserFactorySettingsHandler handler) throws TGBrowserException {
		TGSafBrowserUriRequest request = new TGSafBrowserUriRequest(this.context, new TGSafBrowserUriHandler() {
			public void onUriAccessGranted(Uri uri) {
				if( uri != null ) {
					handler.onCreateSettings(new TGSafBrowserSettings(BROWSER_NAME, uri).toBrowserSettings());
				}
			}
		});
		request.process();
	}
}
