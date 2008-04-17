package org.herac.tuxguitar.player.base;

public abstract class MidiPort {
	private String key;
	private String name;
	
	public MidiPort(String key,String name){
		this.key = key;
		this.name = name;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getName() {
		return this.name;
	}
	
	public abstract void open() throws MidiPlayerException;
	
	public abstract void close() throws MidiPlayerException;
	
	public abstract void check() throws MidiPlayerException;
	
	public abstract MidiOut out() throws MidiPlayerException;
	
}
