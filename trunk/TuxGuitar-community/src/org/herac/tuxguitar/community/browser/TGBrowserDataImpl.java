package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;

public class TGBrowserDataImpl implements TGBrowserData {
	
	public TGBrowserDataImpl(){
		super();
	}
	
	public String getTitle() {
		return "TuxGuitar Community";
	}
	
	public String toString(){
		return getTitle();
	}
}
