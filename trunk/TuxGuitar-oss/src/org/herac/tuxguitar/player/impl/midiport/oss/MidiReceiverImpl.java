package org.herac.tuxguitar.player.impl.midiport.oss;

import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiControllers;

public class MidiReceiverImpl implements GMReceiver{
	
	private boolean connected;
	private MidiOutputPortImpl midiPort;
	private MidiSystem midiSystem;
	
	public MidiReceiverImpl(MidiOutputPortImpl midiPort, MidiSystem midiSystem){
		this.midiPort = midiPort;
		this.midiSystem = midiSystem;
		this.connected = false;
	}
	
	public boolean isConnected(){
		return this.connected;
	}
	
	public void connect(){
		if(!isConnected()){
			this.midiSystem.openPort(this.midiPort);
			this.connected = true;
		}
	}
	
	public void disconnect() {
		if(isConnected()){
			this.midiSystem.closePort();
			this.connected = false;
		}
	}
	
	public void sendAllNotesOff() {
		for(int i = 0; i < 16; i ++){
			sendControlChange(i,MidiControllers.ALL_NOTES_OFF,0);
		}
	}
	
	public void sendControlChange(int channel, int controller, int value) {
		if(isConnected()){
			this.midiSystem.controlChange(channel, controller, value);
		}
	}
	
	public void sendNoteOff(int channel, int key, int velocity) {
		if(isConnected()){
			this.midiSystem.noteOff(channel, key, velocity);
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity) {
		if(isConnected()){
			this.midiSystem.noteOn(channel, key, velocity);
		}
	}
	
	public void sendPitchBend(int channel, int value) {
		if(isConnected()){
			this.midiSystem.pitchBend(channel, value);
		}
	}
	
	public void sendProgramChange(int channel, int value) {
		if(isConnected()){
			this.midiSystem.programChange(channel, value);
		}
	}
	
	public void sendSystemReset() {
		//not implemented
	}
}
