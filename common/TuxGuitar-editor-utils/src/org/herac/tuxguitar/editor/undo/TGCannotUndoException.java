package org.herac.tuxguitar.editor.undo;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGCannotUndoException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public TGCannotUndoException() {
		super();
	}
	
	public TGCannotUndoException(String message) {
		super(message);
	}
	
	public TGCannotUndoException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TGCannotUndoException(Throwable cause) {
		super(cause);
	}
}
