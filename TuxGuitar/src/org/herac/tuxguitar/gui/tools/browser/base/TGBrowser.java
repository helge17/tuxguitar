package org.herac.tuxguitar.gui.tools.browser.base;

import java.util.List;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;

public abstract class TGBrowser {
	
	public TGBrowser(){
		super();
	}
	
	public abstract void open() throws TGBrowserException;
	
	public abstract void close()throws TGBrowserException;
	
	public abstract void cdRoot()throws TGBrowserException;
	
	public abstract void cdUp()throws TGBrowserException;
	
	public abstract void cdElement(TGBrowserElement element)throws TGBrowserException;
	
	public abstract List listElements()throws TGBrowserException;
	
}
