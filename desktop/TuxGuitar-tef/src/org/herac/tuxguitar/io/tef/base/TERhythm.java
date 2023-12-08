package org.herac.tuxguitar.io.tef.base;

public class TERhythm {
	
	private String name;
	private int volume;
	private int instrument;
	
	public TERhythm(String name, int volume, int instrument) {
		this.name = name;
		this.volume = volume;
		this.instrument = instrument;
	}
	
	public int getInstrument() {
		return this.instrument;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public String toString(){
		String string = new String("[RHYTHM]     ");
		string += "\n     Name:       " + getName();
		string += "\n     Volume:     " + getVolume();
		string += "\n     Instrument: " + getInstrument();
		return string;
	}
}
