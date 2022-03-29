package org.herac.tuxguitar.player.impl.midiport.lv2.remote;

public class LV2RemoteException extends Exception {
	
	private static final long serialVersionUID = -4061661103846485675L;

	public LV2RemoteException(){
		super();
	}
	
	public LV2RemoteException(String message){
		super(message);
	}
	
	public LV2RemoteException(Throwable cause){
		super(cause.getMessage(), cause);
	}
	
	public LV2RemoteException(String message, Throwable cause){
		super(message, cause);
	}
}
