package app.tuxguitar.android.browser.model;

import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler;
import app.tuxguitar.tools.browser.base.TGBrowserSettings;

public interface TGBrowserFactory {

	String getName();

	String getType();

	void createSettings(TGBrowserFactorySettingsHandler handler);

	void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings);

}
