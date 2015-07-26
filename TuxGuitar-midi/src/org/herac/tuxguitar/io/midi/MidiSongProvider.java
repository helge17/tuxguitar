package org.herac.tuxguitar.io.midi;

public abstract class MidiSongProvider {
	
	public static final String PROVIDER_ID = "tuxguitar-midi-io";
	
	public MidiSongProvider() {
		super();
	}
	
	public String getProviderId() {
		return PROVIDER_ID;
	}
}
