package org.herac.tuxguitar.android.browser.filesystem;

import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;

public class TGFsBrowserSettings {
	
	private String title;
	private String path;
	
	public TGFsBrowserSettings(String title, String path){
		this.title = title;
		this.path = path;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getPath(){
		return this.path;
	}
	
	public TGBrowserSettings toBrowserSettings() {
		TGBrowserSettings settings = new TGBrowserSettings();
		settings.setTitle(this.getTitle());
		settings.setData(this.getPath());
		return settings;
	}
	
	public static TGFsBrowserSettings createInstance(TGBrowserSettings settings) {
		return new TGFsBrowserSettings(settings.getTitle(), settings.getData());
	}
}
