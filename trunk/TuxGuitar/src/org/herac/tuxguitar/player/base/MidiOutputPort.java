package org.herac.tuxguitar.player.base;

public interface MidiOutputPort extends MidiDevice {
	
	public MidiReceiver getReceiver() throws MidiPlayerException;
	
}