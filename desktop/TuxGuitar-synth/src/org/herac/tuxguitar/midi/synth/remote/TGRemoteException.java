package org.herac.tuxguitar.midi.synth.remote;

public class TGRemoteException extends Exception {
	
	private static final long serialVersionUID = -4061661103846485675L;

	public TGRemoteException(){
		super();
	}
	
	public TGRemoteException(String message){
		super(message);
	}
	
	public TGRemoteException(Throwable cause){
		super(cause.getMessage(), cause);
	}
	
	public TGRemoteException(String message, Throwable cause){
		super(message, cause);
	}
}
