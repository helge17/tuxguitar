package org.herac.tuxguitar.player.base;

public class MidiPlayerException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public MidiPlayerException(String message) {
		super(message);
	}
	
	public MidiPlayerException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
