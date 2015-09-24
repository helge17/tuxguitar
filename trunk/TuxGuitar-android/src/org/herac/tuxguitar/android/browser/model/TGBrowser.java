package org.herac.tuxguitar.android.browser.model;

import java.util.List;

public interface TGBrowser {
	
	void open() throws TGBrowserException;
	
	void close() throws TGBrowserException;
	
	void cdRoot() throws TGBrowserException;
	
	void cdUp() throws TGBrowserException;
	
	void cdElement(TGBrowserElement element) throws TGBrowserException;
	
	List<TGBrowserElement> listElements() throws TGBrowserException;
	
	TGBrowserElement createElement(String name) throws TGBrowserException;
	
	boolean isWritable() throws TGBrowserException;
}
