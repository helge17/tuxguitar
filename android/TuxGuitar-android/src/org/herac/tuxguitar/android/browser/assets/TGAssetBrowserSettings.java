package org.herac.tuxguitar.android.browser.assets;

import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;

public class TGAssetBrowserSettings  {

	private static final String DEFAULT_ID = "browser-assets";
	private static final String DEFAULT_PATH = "demo-songs";
	private static final String DEFAULT_TITLE = (TGAssetBrowserFactory.BROWSER_NAME + " (Read Only)");
	
	public TGAssetBrowserSettings(){
		super();
	}
	
	public String getId(){
		return DEFAULT_ID;
	}
	
	public String getTitle(){
		return DEFAULT_TITLE;
	}
	
	public String getPath(){
		return DEFAULT_PATH;
	}
	
	public boolean equals(Object o) {
		return (this.hashCode() == o.hashCode());
	}
	
	public int hashCode() {
		return (TGAssetBrowserSettings.class.getName() + "-" + this.getId()).hashCode();
	}
	
	public TGBrowserSettings toBrowserSettings() {
		TGBrowserSettings settings = new TGBrowserSettings();
		settings.setTitle(this.getTitle());
		settings.setData(this.getId());
		return settings;
	}
}
