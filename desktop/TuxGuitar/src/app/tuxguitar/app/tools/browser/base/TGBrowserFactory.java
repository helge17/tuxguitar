package app.tuxguitar.app.tools.browser.base;

import app.tuxguitar.ui.resource.UIImage;

public interface TGBrowserFactory {

	String getName();

	UIImage getIcon();

	String getType();

	void createSettings(TGBrowserFactorySettingsHandler handler);

	void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings);
}
