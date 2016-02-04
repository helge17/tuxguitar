package org.herac.tuxguitar.app.tools.browser.filesystem;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserSettings;

public class TGBrowserSettingsModel {
	
	private String title;
	private String path;
	
	public TGBrowserSettingsModel(String title, String path){
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
	
	public static TGBrowserSettingsModel createInstance(TGBrowserSettings settings) {
		return new TGBrowserSettingsModel(settings.getTitle(), settings.getData());
	}
}
