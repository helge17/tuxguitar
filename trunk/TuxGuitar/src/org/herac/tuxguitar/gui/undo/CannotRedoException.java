/*
 * Created on 05-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.undo;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CannotRedoException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public CannotRedoException() {
		super();
	}
	
	public CannotRedoException(String message) {
		super(message);
	}
	
	public CannotRedoException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CannotRedoException(Throwable cause) {
		super(cause);
	}
}
