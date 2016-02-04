package org.herac.tuxguitar.app.tools.browser;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserSettings;

public class TGBrowserCollection {
	
	private String type;
	private TGBrowserSettings data;
	
	public TGBrowserCollection(){
		super();
	}
	
	public TGBrowserSettings getData() {
		return this.data;
	}
	
	public void setData(TGBrowserSettings data) {
		this.data = data;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}	
}
