package org.herac.tuxguitar.midi.synth;

public interface TGMidiProcessor extends TGAudioProcessor {
	
	void sendNoteOn(int key, int velocity, int voice, boolean bendMode);
	
	void sendNoteOff(int key, int velocity, int voice, boolean bendMode);
	
	void sendPitchBend(int value, int voice, boolean bendMode);
	
	void sendControlChange(int controller, int value);
}
