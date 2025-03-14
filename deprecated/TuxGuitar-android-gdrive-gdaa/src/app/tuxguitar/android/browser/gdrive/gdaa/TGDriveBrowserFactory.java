package app.tuxguitar.android.browser.gdrive.gdaa;

import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserException;
import app.tuxguitar.android.browser.model.TGBrowserFactory;
import app.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import app.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import app.tuxguitar.android.browser.model.TGBrowserSettings;
import app.tuxguitar.util.TGContext;

public class TGDriveBrowserFactory implements TGBrowserFactory{

	public static final String BROWSER_TYPE = "google-drive-gdaa";
	public static final String BROWSER_NAME = "Google Drive";

	private TGContext context;

	public TGDriveBrowserFactory(TGContext context) {
		this.context = context;
	}

	public String getType(){
		return BROWSER_TYPE;
	}

	public String getName(){
		return BROWSER_NAME;
	}

	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings data) throws TGBrowserException {
		handler.onCreateBrowser(new TGDriveBrowser(this.context, TGDriveBrowserSettings.createInstance(data)));
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) throws TGBrowserException {
		TGDriveBrowserSettings defaultSettings = new TGDriveBrowserSettings();
		TGBrowserSettings settings = defaultSettings.toBrowserSettings();
		if( TGBrowserManager.getInstance(this.context).getCollection(this.getType(), settings) != null ) {
			throw new TGBrowserException(settings.getTitle() + " already exists.");
		}

		handler.onCreateSettings(settings);
	}
}
