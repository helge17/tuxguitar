package app.tuxguitar.community.browser;

import app.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactoryHandler;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactorySettingsHandler;
import app.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import app.tuxguitar.util.TGContext;

public class TGBrowserFactoryImpl implements TGBrowserFactory {

	private TGContext context;
	private TGBrowserSettings data;

	public TGBrowserFactoryImpl(TGContext context){
		this.context = context;
		this.data = new TGBrowserSettingsModel().toBrowserSettings();
	}

	public String getName() {
		return "Community Files";
	}

	public String getType() {
		return "community";
	}

	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings) {
		handler.onCreateBrowser(new TGBrowserImpl(this.context));
	}

	public void createSettings(final TGBrowserFactorySettingsHandler handler) {
		new TGBrowserAuthDialog(this.context, new Runnable() {
			public void run() {
				handler.onCreateSettings(TGBrowserFactoryImpl.this.data);
			}
		}).open();
	}
}
