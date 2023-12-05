package org.herac.tuxguitar.player.impl.midiport.fluidsynth.type;

public class DoubleRef {
	
	private double value;
	
	public DoubleRef(){
		this.value = 0;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
}
