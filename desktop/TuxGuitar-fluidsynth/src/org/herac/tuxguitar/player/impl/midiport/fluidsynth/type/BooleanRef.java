package org.herac.tuxguitar.player.impl.midiport.fluidsynth.type;

public class BooleanRef {
	
	private boolean value;
	
	public BooleanRef(){
		this.value = false;
	}
	
	public boolean getValue() {
		return this.value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}
}
