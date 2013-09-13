package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.player.base.MidiControllers;

public class JackReceiver implements GMReceiver{
	
	private JackClient jackClient;
	private JackOutputPort jackOutputPort;
	
	public JackReceiver(JackClient jackClient, JackOutputPort jackOutputPort){
		this.jackClient = jackClient;
		this.jackOutputPort = jackOutputPort;
	}
	
	public void sendAllNotesOff() {
		for(int i = 0; i < 16; i ++){
			sendControlChange(i,MidiControllers.ALL_NOTES_OFF,0);
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity) {
		byte[] event = new byte[3];
		event[0] = (byte)(0x90 | this.jackOutputPort.getRouter().getChannelRoute(channel) );
		event[1] = (byte)key;
		event[2] = (byte)velocity;
//		this.jackClient.addEventToQueue( this.jackOutputPort.getRouter().getPortRoute(channel) , event);
	}
	
	public void sendNoteOff(int channel, int key, int velocity) {
		byte[] event = new byte[3];
		event[0] = (byte)(0x80 | this.jackOutputPort.getRouter().getChannelRoute(channel) );
		event[1] = (byte)key;
		event[2] = (byte)velocity;
//		this.jackClient.addEventToQueue( this.jackOutputPort.getRouter().getPortRoute(channel) , event);
	}
	
	public void sendPitchBend(int channel, int value) {
		byte[] event = new byte[3];
		event[0] = (byte)(0xE0 | this.jackOutputPort.getRouter().getChannelRoute(channel) );
		event[1] = (byte)0;
		event[2] = (byte)value;
//		this.jackClient.addEventToQueue( this.jackOutputPort.getRouter().getPortRoute(channel) , event);
	}
	
	public void sendProgramChange(int channel, int value) {
		this.jackOutputPort.getRouter().setProgram(channel, value);
		
		byte[] event1 = new byte[3];
		event1[0] = (byte)(0xB0 | this.jackOutputPort.getRouter().getChannelRoute(channel) );
		event1[1] = (byte)MidiControllers.BANK_SELECT;
		event1[2] = (byte)this.jackOutputPort.getRouter().getBankRoute(channel);
		
		byte[] event2 = new byte[2];
		event2[0] = (byte)(0xC0 | this.jackOutputPort.getRouter().getChannelRoute(channel) );
		event2[1] = (byte)this.jackOutputPort.getRouter().getProgramRoute(channel , value);
		
//		this.jackClient.addEventToQueue( this.jackOutputPort.getRouter().getPortRoute(channel) , event1);
//		this.jackClient.addEventToQueue( this.jackOutputPort.getRouter().getPortRoute(channel) , event2);
	}
	
	public void sendControlChange(int channel, int controller, int value) {
		if( controller == MidiControllers.BANK_SELECT){
			this.jackOutputPort.getRouter().setBank(channel, value);
		}else{
			byte[] event = new byte[3];
			event[0] = (byte)(0xB0 | this.jackOutputPort.getRouter().getChannelRoute(channel) );
			event[1] = (byte)controller;
			event[2] = (byte)value;
//			this.jackClient.addEventToQueue( this.jackOutputPort.getRouter().getPortRoute(channel) , event);
		}
	}
	
	public void sendSystemReset() {
		//not implemented
	}
}
