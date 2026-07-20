package app.tuxguitar.android.browser.assets;

import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserFactory;
import app.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import app.tuxguitar.tools.browser.TGBrowserCollection;
import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler;
import app.tuxguitar.tools.browser.base.TGBrowserSettings;
import app.tuxguitar.util.TGContext;

public class TGAssetBrowserFactory implements TGBrowserFactory{

	public static final String BROWSER_TYPE = "assets";

	private TGContext context;
	private TGAssetBrowserSettings settings;

	public TGAssetBrowserFactory(TGContext context) {
		this.context = context;
		this.settings = new TGAssetBrowserSettings(context);
	}

	public String getType(){
		return BROWSER_TYPE;
	}

	public String getName(){
		return this.getDefaultSettings().getTitle();
	}

	public TGAssetBrowserSettings getDefaultSettings() {
		return this.settings;
	}

	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings data) {
		handler.onCreateBrowser(new TGAssetBrowser(this.context, this.getDefaultSettings()));
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) {
		handler.onCreateSettings(this.getDefaultSettings().toBrowserSettings());
	}

	public TGBrowserCollection createDemoCollection() {
		return TGBrowserManager.getInstance(this.context).createCollection(this.getType(), this.getDefaultSettings().toBrowserSettings());
	}
}
