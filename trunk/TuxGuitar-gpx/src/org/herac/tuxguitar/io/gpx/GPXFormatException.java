package org.herac.tuxguitar.io.gpx;

import org.herac.tuxguitar.io.base.TGFileFormatException;

public class GPXFormatException extends TGFileFormatException {
	
	private static final long serialVersionUID = 1L;
	
	public GPXFormatException(String message) {
		super(message);
	}
	
	public GPXFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
