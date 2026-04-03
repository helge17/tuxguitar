package app.tuxguitar.app.tools.browser.filesystem;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactoryHandler;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactorySettingsHandler;
import app.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.util.TGContext;

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

	public UIImage getIcon(){
		return TuxGuitar.getInstance().getIconManager().getBrowserFolder();
	}

	public void createSettings(TGBrowserFactorySettingsHandler handler) {
		new TGBrowserDataDialog(this.context, handler).open();
	}

	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings) {
		handler.onCreateBrowser(new TGBrowserImpl(TGBrowserSettingsModel.createInstance(settings)));
	}
}
