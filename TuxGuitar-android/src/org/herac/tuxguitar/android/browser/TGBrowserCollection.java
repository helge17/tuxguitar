package org.herac.tuxguitar.android.browser;

import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;

public class TGBrowserCollection {
	
	private String type;
	private TGBrowserSettings data;
	
	public TGBrowserCollection(){
		super();
	}
	
	public TGBrowserSettings getSettings() {
		return this.data;
	}
	
	public void setSettings(TGBrowserSettings data) {
		this.data = data;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
