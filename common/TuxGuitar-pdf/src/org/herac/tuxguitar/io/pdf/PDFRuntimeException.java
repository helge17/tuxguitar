package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.util.TGException;

public class PDFRuntimeException extends TGException {
	
	private static final long serialVersionUID = 7678108131867164344L;
	
	public PDFRuntimeException(){
		super();
	}
	
	public PDFRuntimeException(String message){
		super(message);
	}
	
	public PDFRuntimeException(Throwable cause){
		super(cause.getMessage(), cause);
	}
	
	public PDFRuntimeException(String message, Throwable cause){
		super(message, cause);
	}
}
