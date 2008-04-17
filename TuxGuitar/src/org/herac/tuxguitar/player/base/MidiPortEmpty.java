package org.herac.tuxguitar.player.base;


public class MidiPortEmpty extends MidiPort{
	
	public MidiPortEmpty() {
		super(null,null);
	}
	
	public MidiOut out() throws MidiPlayerException {
		return new MidiOutEmpty();
	}
	
	public void open() throws MidiPlayerException{
		// Not implemented
	}
	
	public void close() throws MidiPlayerException{
		// Not implemented
	}
	
	public void check() throws MidiPlayerException {
		// Not implemented
	}
}
class MidiOutEmpty implements MidiOut{
	
	public void sendAllNotesOff() throws MidiPlayerException{
		// Not implemented
	}
	
	public void sendControlChange(int channel, int controller, int value) throws MidiPlayerException{
		// Not implemented
	}
	
	public void sendNoteOff(int channel, int key, int velocity) throws MidiPlayerException{
		System.out.println("No sound note");
		// Not implemented
	}
	
	public void sendNoteOn(int channel, int key, int velocity) throws MidiPlayerException{
		// Not implemented
	}
	
	public void sendPitchBend(int channel, int value) throws MidiPlayerException{
		// Not implemented
	}
	
	public void sendProgramChange(int channel, int value) throws MidiPlayerException{
		// Not implemented
	}
	
	public void sendSystemReset() throws MidiPlayerException{
		// Not implemented
	}
	
}
