package org.herac.tuxguitar.player.impl.midiport.fluidsynth.type;

public class IntegerRef {
	
	private int value;
	
	public IntegerRef(){
		this.value = 0;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}
