package org.herac.tuxguitar.android.browser.model;

import java.io.InputStream;
import java.io.OutputStream;

public interface TGBrowserElement {
	
	String getName();
	
	boolean isFolder();
	
	boolean isWritable();
	
	InputStream getInputStream() throws TGBrowserException;
	
	OutputStream getOutputStream() throws TGBrowserException;
	
}
