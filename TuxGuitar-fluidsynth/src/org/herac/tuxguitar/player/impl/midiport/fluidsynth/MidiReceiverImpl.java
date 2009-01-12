package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiReceiver;

public class MidiReceiverImpl implements MidiReceiver{
	
	private MidiSynth synth;
	
	public MidiReceiverImpl(MidiSynth synth){
		this.synth = synth;
	}
	
	public void sendSystemReset() {
		this.synth.sendSystemReset();
	}
	
	public void sendNoteOn(int channel, int key, int velocity) {
		this.synth.sendNoteOn(channel, key, velocity);
	}
	
	public void sendNoteOff(int channel, int key, int velocity) {
		this.synth.sendNoteOff(channel, key, velocity);
	}
	
	public void sendControlChange(int channel, int controller, int value) {
		this.synth.sendControlChange(channel, controller, value);
	}
	
	public void sendProgramChange(int channel, int value) {
		this.synth.sendProgramChange(channel, value);
	}
	
	public void sendPitchBend(int channel, int value) {
		this.synth.sendPitchBend(channel, value);
	}
	
	public void sendAllNotesOff() {
		for(int i = 0; i < 16; i ++){
			this.sendControlChange(i,MidiControllers.ALL_NOTES_OFF,0);
		}
	}
}
