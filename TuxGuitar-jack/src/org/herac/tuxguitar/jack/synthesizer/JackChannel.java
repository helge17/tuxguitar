package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.JackPort;
import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public class JackChannel implements MidiChannel{
	
	private int channel1;
	private int channel2;
	private JackPort jackPort;
	private JackClient jackClient;
	
	public JackChannel(JackClient jackClient, JackPort jackPort){
		this.jackClient = jackClient;
		this.jackPort = jackPort;
		this.channel1 = 0;
		this.channel2 = 0;
	}
	
	public void sendAllNotesOff() throws MidiPlayerException {
		this.sendControlChange(MidiControllers.ALL_NOTES_OFF,0);
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		byte[] event = new byte[3];
		event[0] = (byte)(0x90 | resolveChannel(bendMode) );
		event[1] = (byte)key;
		event[2] = (byte)velocity;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		byte[] event = new byte[3];
		event[0] = (byte)(0x80 | resolveChannel(bendMode) );
		event[1] = (byte)key;
		event[2] = (byte)velocity;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) throws MidiPlayerException {
		byte[] event = new byte[3];
		event[0] = (byte)(0xE0 | resolveChannel(bendMode) );
		event[1] = (byte)0;
		event[2] = (byte)value;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendProgramChange(int value) throws MidiPlayerException {
		this.sendProgramChange(value, this.channel1);
		if( this.channel1 != this.channel2 ){
			this.sendProgramChange(value, this.channel2);
		}
	}

	public void sendProgramChange(int value, int channel) throws MidiPlayerException {
		byte[] event = new byte[2];
		event[0] = (byte)(0xC0 | channel );
		event[1] = (byte)value;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendControlChange(int controller, int value) throws MidiPlayerException {
		this.sendControlChange(controller, value, this.channel1);
		if( this.channel1 != this.channel2 ){
			this.sendControlChange(controller, value, this.channel2);
		}
	}
	
	public void sendControlChange(int controller, int value, int channel) throws MidiPlayerException {
		byte[] event = new byte[3];
		event[0] = (byte)(0xB0 | channel );
		event[1] = (byte)controller;
		event[2] = (byte)value;
		this.jackClient.addEventToQueue( this.jackPort , event);
	}
	
	public void sendParameter(String key, String value) throws MidiPlayerException{
		if( JackChannelParameter.PARAMETER_GM_CHANNEL_1.equals(key) ){
			this.channel1 = Integer.parseInt(value);
		}
		if( JackChannelParameter.PARAMETER_GM_CHANNEL_2.equals(key) ){
			this.channel2 = Integer.parseInt(value);
		}
	}
	
	private int resolveChannel(boolean bendMode){
		return (bendMode ? this.channel2 : this.channel1);
	}
}
