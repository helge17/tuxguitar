package org.herac.tuxguitar.player.impl.midiport.vst.remote;

public enum VSTType {
	
	VST("VST"), 
	
	VSTI("VSTi");
	
	private String label;
	
	private VSTType(String label) {
		this.label = label;
	}
	
	public String toString() {
		return this.label;
	}
}
