package org.herac.tuxguitar.gui.tools.browser.base;

import java.io.InputStream;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;

public abstract class TGBrowserElement {
	
	private String name;
	
	public TGBrowserElement(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public abstract boolean isFolder();
	
	public abstract InputStream getInputStream() throws TGBrowserException;
	
}
