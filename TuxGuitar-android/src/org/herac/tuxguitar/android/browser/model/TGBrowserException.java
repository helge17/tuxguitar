package org.herac.tuxguitar.android.browser.model;

public class TGBrowserException extends Exception{
	
	private static final long serialVersionUID = -7001669833827510215L;

	public TGBrowserException() {
		super();
	}
	
	public TGBrowserException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TGBrowserException(String message) {
		super(message);
	}
	
	public TGBrowserException(Throwable cause) {
		super(cause);
	}
}
