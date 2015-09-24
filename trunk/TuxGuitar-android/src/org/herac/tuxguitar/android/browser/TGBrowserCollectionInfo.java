package org.herac.tuxguitar.android.browser;

public class TGBrowserCollectionInfo {
	
	private String type;
	private String settings;
	
	public TGBrowserCollectionInfo(){
		super();
	}
	
	public String getSettings() {
		return this.settings;
	}
	
	public void setSettings(String data) {
		this.settings = data;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
