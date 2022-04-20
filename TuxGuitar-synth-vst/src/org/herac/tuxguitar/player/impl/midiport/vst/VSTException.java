package org.herac.tuxguitar.player.impl.midiport.vst;

public class VSTException extends Exception {
	
	private static final long serialVersionUID = 1449680970167598695L;
	
	public VSTException(){
		super();
	}
	
	public VSTException(String message){
		super(message);
	}
	
	public VSTException(Throwable cause){
		super(cause.getMessage(), cause);
	}
	
	public VSTException(String message, Throwable cause){
		super(message, cause);
	}
}
