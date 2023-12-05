package org.herac.tuxguitar.app.tools.browser.ftp;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;

public class TGBrowserElementImpl implements TGBrowserElement{
	
	private String name;
	private String path;
	private String info;
	
	public TGBrowserElementImpl(String name, String info, String path) {
		this.name = name;
		this.info = info;
		this.path = path;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public boolean isFolder(){
		return (this.info != null && this.info.length() > 0 && this.info.charAt(0) == 'd');
	}
	
	public boolean isSymLink() {
		return (this.info != null && this.info.length() > 0 && this.info.charAt(0) == 'l');
	}
}
