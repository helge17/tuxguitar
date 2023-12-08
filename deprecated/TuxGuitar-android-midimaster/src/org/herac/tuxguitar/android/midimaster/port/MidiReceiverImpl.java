package org.herac.tuxguitar.android.midimaster.port;

import org.billthefarmer.mididriver.MidiDriver;
import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiControllers;

public class MidiReceiverImpl implements GMReceiver{
	
	private boolean connected;
	private MidiDriver midiDriver;
	
	public MidiReceiverImpl(){
		this.midiDriver = new MidiDriver();
		this.connected = false;
	}
	
	public boolean isConnected(){
		return this.connected;
	}
	
	public void connect(){
		if(!isConnected()){
			this.startMidiDriver();
			this.connected = true;
		}
	}
	
	public void disconnect() {
		if( isConnected() ){
			this.stopMidiDriver();
			this.connected = false;
		}
	}
	
	public void sendAllNotesOff() {
		for(int i = 0; i < 16; i ++){
			this.sendControlChange(i,MidiControllers.ALL_NOTES_OFF,0);
		}
	}
	
	public void sendControlChange(int channel, int controller, int value) {
		if( isConnected()){
			byte[] event = new byte[3];
			event[0] = (byte)(0xB0 | channel );
			event[1] = (byte)controller;
			event[2] = (byte)value;
			this.sendEvent(event);
		}
	}
	
	public void sendNoteOff(int channel, int key, int velocity) {
		if( isConnected()){
			byte[] event = new byte[3];
			event[0] = (byte)(0x80 | channel );
			event[1] = (byte)key;
			event[2] = (byte)velocity;
			this.sendEvent(event);
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity) {
		if( isConnected()){			
			byte[] event = new byte[3];
			event[0] = (byte)(0x90 | channel );
			event[1] = (byte)key;
			event[2] = (byte)velocity;
			this.sendEvent(event);
		}
	}
	
	public void sendPitchBend(int channel, int value) {
		if( isConnected()){
			byte[] event = new byte[3];
			event[0] = (byte)(0xE0 | channel );
			event[1] = (byte)0;
			event[2] = (byte)value;
			this.sendEvent(event);
		}
	}
	
	public void sendProgramChange(int channel, int value) {
		if( isConnected()){
			byte[] event = new byte[2];
			event[0] = (byte)(0xC0 | channel );
			event[1] = (byte)value;
			this.sendEvent(event);
		}
	}
	
	public void sendSystemReset() {
		//not implemented
	}
	
	public void sendEvent(final byte[] event) {
		this.midiDriver.queueEvent(event);
	}
	
	public void startMidiDriver() {
		this.midiDriver.start();
	}
	
	public void stopMidiDriver() {
		this.midiDriver.stop();
	}
}
