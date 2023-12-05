package org.herac.tuxguitar.ui;

import java.net.URL;

import org.herac.tuxguitar.ui.appearance.UIAppearance;

public interface UIApplication extends UIComponent {
	
	UIFactory getFactory();
	
	UIAppearance getAppearance();
	
	void openUrl(URL url);
	
	void start(Runnable runnable);
	
	void runInUiThread(Runnable runnable);
	
	boolean isInUiThread();
}
