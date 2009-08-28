package org.herac.tuxguitar.io.base;

public class TGFileFormatException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public TGFileFormatException() {
		super();
	}
	
	public TGFileFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TGFileFormatException(String message) {
		super(message);
	}
	
	public TGFileFormatException(Throwable cause) {
		super(cause);
	}
	
}
