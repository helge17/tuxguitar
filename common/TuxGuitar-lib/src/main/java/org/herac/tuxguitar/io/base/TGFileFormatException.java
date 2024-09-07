package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGMessagesManager;

public class TGFileFormatException extends TGException{
	
	public static final String UNSUPPORTED_VERSION = "error.unsupported-version";
	public static final String UNSUPPORTED_FORMAT = "error.unsupported-format";
	
	private static final long serialVersionUID = 1L;
	
	public TGFileFormatException() {
		super();
	}
	
	public TGFileFormatException(String message, Throwable cause) {
		super(TGMessagesManager.getProperty(message), cause);
	}
	
	public TGFileFormatException(String message) {
		super(TGMessagesManager.getProperty(message));
	}
	
	public TGFileFormatException(Throwable cause) {
		super(cause);
	}
}
