package org.herac.tuxguitar.player.base;


public interface MidiChannel {
	
	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException;
	
	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException;
	
	public void sendPitchBend(int value, int voice, boolean bendMode) throws MidiPlayerException;
	
	public void sendProgramChange(int value) throws MidiPlayerException;
	
	public void sendControlChange(int controller, int value) throws MidiPlayerException;
	
	public void sendParameter(String key, String value) throws MidiPlayerException;
	
	public void sendAllNotesOff() throws MidiPlayerException;
	
}
