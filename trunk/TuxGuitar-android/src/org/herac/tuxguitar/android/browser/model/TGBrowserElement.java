package org.herac.tuxguitar.android.browser.model;

import java.io.InputStream;
import java.io.OutputStream;

public interface TGBrowserElement {
	
	String getName() throws TGBrowserException;
	
	boolean isFolder() throws TGBrowserException;
	
	boolean isWritable() throws TGBrowserException;
	
	InputStream getInputStream() throws TGBrowserException;
	
	OutputStream getOutputStream() throws TGBrowserException;
	
}
