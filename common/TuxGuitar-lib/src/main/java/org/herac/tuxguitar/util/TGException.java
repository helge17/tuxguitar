package org.herac.tuxguitar.util;

public class TGException extends RuntimeException {
	
	private static final long serialVersionUID = 8298443126251976034L;

	public TGException(){
		super();
	}
	
	public TGException(String message){
		super(message);
	}
	
	public TGException(Throwable cause){
		super(cause.getMessage(), cause);
	}
	
	public TGException(String message, Throwable cause){
		super(message, cause);
	}
}
