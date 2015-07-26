package org.herac.tuxguitar.editor.undo;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGCannotRedoException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public TGCannotRedoException() {
		super();
	}
	
	public TGCannotRedoException(String message) {
		super(message);
	}
	
	public TGCannotRedoException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TGCannotRedoException(Throwable cause) {
		super(cause);
	}
}
