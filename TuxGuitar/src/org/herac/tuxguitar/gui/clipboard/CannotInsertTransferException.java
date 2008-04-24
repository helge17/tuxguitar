/*
 * Created on 05-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.clipboard;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CannotInsertTransferException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public CannotInsertTransferException() {
		super();
	}
	
	public CannotInsertTransferException(String message) {
		super(message);
	}
	
	public CannotInsertTransferException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CannotInsertTransferException(Throwable cause) {
		super(cause);
	}
}
