package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.JackPort;
import org.herac.tuxguitar.player.base.MidiControllers;

public class JackGmReceiver implements GMReceiver{
	
	private JackClient jackClient;
	private JackPort jackPort;
	
	public JackGmReceiver(JackClient jackClient, JackPort jackPort){
		this.jackClient = jackClient;
		this.jackPort = jackPort;
	}
	
	public void sendAllNotesOff() {
		for(int i = 0; i < 16; i ++){
			sendControlChange(i,MidiControllers.ALL_NOTES_OFF,0);
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity) {
		byte[] event = new byte[3];
		event[0] = (byte)(0x90 | channel );
		event[1] = (byte)key;
		event[2] = (byte)velocity;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendNoteOff(int channel, int key, int velocity) {
		byte[] event = new byte[3];
		event[0] = (byte)(0x80 | channel );
		event[1] = (byte)key;
		event[2] = (byte)velocity;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendPitchBend(int channel, int value) {
		byte[] event = new byte[3];
		event[0] = (byte)(0xE0 | channel );
		event[1] = (byte)0;
		event[2] = (byte)value;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendProgramChange(int channel, int value) {
		byte[] event = new byte[2];
		event[0] = (byte)(0xC0 | channel );
		event[1] = (byte)value;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendControlChange(int channel, int controller, int value) {
		byte[] event = new byte[3];
		event[0] = (byte)(0xB0 | channel );
		event[1] = (byte)controller;
		event[2] = (byte)value;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendSystemReset() {
		//not implemented
	}
}
