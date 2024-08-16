package org.herac.tuxguitar.util.plugin;

import org.herac.tuxguitar.util.TGException;

public class TGPluginException extends TGException{
	
	private static final long serialVersionUID = 1L;
	
	public TGPluginException() {
		super();
	}
	
	public TGPluginException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TGPluginException(String message) {
		super(message);
	}
	
	public TGPluginException(Throwable cause) {
		super(cause);
	}
}
