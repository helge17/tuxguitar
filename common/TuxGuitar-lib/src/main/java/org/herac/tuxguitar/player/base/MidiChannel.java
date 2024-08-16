package org.herac.tuxguitar.player.base;

public interface MidiChannel {
	
	void sendNoteOn(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException;
	
	void sendNoteOff(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException;
	
	void sendPitchBend(int value, int voice, boolean bendMode) throws MidiPlayerException;
	
	void sendProgramChange(int value) throws MidiPlayerException;
	
	void sendControlChange(int controller, int value) throws MidiPlayerException;
	
	void sendParameter(String key, String value) throws MidiPlayerException;
	
	void sendAllNotesOff() throws MidiPlayerException;
}
