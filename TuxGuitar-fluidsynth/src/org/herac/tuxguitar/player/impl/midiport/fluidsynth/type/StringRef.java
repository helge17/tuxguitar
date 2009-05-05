package org.herac.tuxguitar.player.impl.midiport.fluidsynth.type;

public class StringRef {
	
	private String value;
	
	public StringRef(){
		this.value = new String();
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
