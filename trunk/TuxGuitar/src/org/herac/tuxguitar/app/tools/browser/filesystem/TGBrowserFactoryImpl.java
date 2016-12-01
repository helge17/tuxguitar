package org.herac.tuxguitar.app.tools.browser.filesystem;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactoryHandler;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserFactoryImpl implements TGBrowserFactory{
	
	private TGContext context;
	
	public TGBrowserFactoryImpl(TGContext context) {
		this.context = context;
	}
	
	public String getType(){
		return "file.system";
	}
	
	public String getName(){
		return TuxGuitar.getProperty("browser.factory.fs.name");
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) {
		new TGBrowserDataDialog(this.context, handler).open();
	}

	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings) {
		handler.onCreateBrowser(new TGBrowserImpl(TGBrowserSettingsModel.createInstance(settings)));
	}
}
