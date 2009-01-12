package org.herac.tuxguitar.player.base;

public abstract class MidiOutputPort extends MidiPort{
	
	public MidiOutputPort(String key, String name) {
		super(key, name);
	}
	
	public abstract void check() throws MidiPlayerException;
	
	public abstract MidiReceiver getReceiver() throws MidiPlayerException;
	
}
