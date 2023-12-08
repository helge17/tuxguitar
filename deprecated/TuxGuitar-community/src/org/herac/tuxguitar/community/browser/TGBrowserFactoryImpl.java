package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactoryHandler;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;

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
