package org.herac.tuxguitar.android.view.dialog.browser.collection;

import org.herac.tuxguitar.android.browser.TGBrowserCollection;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGBrowserCollectionsSettingsHandler implements TGBrowserFactorySettingsHandler {
	
	private TGBrowserCollectionsDialog dialog;
	private String type;
	
	public TGBrowserCollectionsSettingsHandler(TGBrowserCollectionsDialog dialog, String type) {
		this.dialog = dialog;
		this.type = type;
	}
	
	public void onCreateSettings(TGBrowserSettings settings) {
		TGBrowserManager browserManager = TGBrowserManager.getInstance(this.dialog.findContext());
		TGBrowserCollection collection = browserManager.createCollection(this.type, settings);
		
		this.dialog.addCollection(collection);
	}

	@Override
	public void handleError(Throwable throwable) {
		TGErrorManager.getInstance(this.dialog.findContext()).handleError(throwable);
	}
}
