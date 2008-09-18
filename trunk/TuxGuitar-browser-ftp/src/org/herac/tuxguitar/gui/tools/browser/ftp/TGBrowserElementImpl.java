package org.herac.tuxguitar.gui.tools.browser.ftp;

import java.io.InputStream;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

public class TGBrowserElementImpl extends TGBrowserElement{
	
	private TGBrowserImpl browser;
	private String path;
	private String info;
	
	public TGBrowserElementImpl(TGBrowserImpl browser,String name,String info,String path) {
		super(name);
		this.browser = browser;
		this.info = info;
		this.path = path;
	}
	
	public boolean isFolder(){
		return (this.info != null && this.info.length() > 0 && this.info.charAt(0) == 'd');
	}
	
	public boolean isSymLink() {
		return (this.info != null && this.info.length() > 0 && this.info.charAt(0) == 'l');
	}
	
	public InputStream getInputStream()throws TGBrowserException {
		return this.browser.getInputStream(this.path,this);
	}
	
}
