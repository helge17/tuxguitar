package org.herac.tuxguitar.action;

import org.herac.tuxguitar.util.TGException;

public class TGActionException extends TGException {
	
	private static final long serialVersionUID = 8298443126251976034L;

	public TGActionException(String message){
		super(message);
	}
	
	public TGActionException(Throwable cause){
		super(cause);
	}
	
	public TGActionException(String message, Throwable cause){
		super(message, cause);
	}
}
