package org.herac.tuxguitar.io.midi;

public class MidiSettings {
	
	private int transpose;
	
	public MidiSettings(){
		this.transpose = 0;
	}
	
	public int getTranspose() {
		return this.transpose;
	}
	
	public void setTranspose(int transpose) {
		this.transpose = transpose;
	}
	
	public static MidiSettings getDefaults(){
		return new MidiSettings();
	}
}
