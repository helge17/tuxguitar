package org.herac.tuxguitar.app.tools.browser.base;

import org.herac.tuxguitar.ui.resource.UIImage;

public interface TGBrowserFactory {

	String getName();

	UIImage getIcon();

	String getType();

	void createSettings(TGBrowserFactorySettingsHandler handler);

	void createBrowser(TGBrowserFactoryHandler handler, TGBrowserSettings settings);
}
