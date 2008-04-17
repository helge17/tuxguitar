package org.herac.tuxguitar.gui.help.doc;


public class DocException extends Exception{

	private static final long serialVersionUID = 1L;

	public DocException() {
		super();
	}

	public DocException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocException(String message) {
		super(message);
	}

	public DocException(Throwable cause) {
		super(cause);
	}
}
