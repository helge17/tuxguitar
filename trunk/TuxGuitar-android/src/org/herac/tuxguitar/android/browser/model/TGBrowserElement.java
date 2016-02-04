package org.herac.tuxguitar.android.browser.model;

public interface TGBrowserElement {
	
	String getName() throws TGBrowserException;
	
	boolean isFolder() throws TGBrowserException;
	
	boolean isWritable() throws TGBrowserException;
}
