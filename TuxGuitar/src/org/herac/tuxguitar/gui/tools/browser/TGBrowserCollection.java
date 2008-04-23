package org.herac.tuxguitar.gui.tools.browser;

import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;

public class TGBrowserCollection {
	
	private String type;
	private TGBrowserData data;
	
	public TGBrowserCollection(){
		super();
	}
	
	public TGBrowserData getData() {
		return this.data;
	}
	
	public void setData(TGBrowserData data) {
		this.data = data;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
