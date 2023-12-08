package org.herac.tuxguitar.android.midi.port;

import android.annotation.SuppressLint;

import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiPlayerException;

import java.io.IOException;

@SuppressLint("NewApi")
public class MidiReceiverImpl implements GMReceiver{

	private MidiOutputPortConection connection;
	
	public MidiReceiverImpl(MidiOutputPortConection connection){
		this.connection = connection;
	}

	public void sendAllNotesOff() throws MidiPlayerException {
		for(int i = 0; i < 16; i ++){
			this.sendControlChange(i,MidiControllers.ALL_NOTES_OFF, 0);
		}
	}
	
	public void sendControlChange(int channel, int controller, int value) throws MidiPlayerException {
		if( this.connection.isConnected()){
			byte[] event = new byte[3];
			event[0] = (byte)(0xB0 | channel );
			event[1] = (byte)controller;
			event[2] = (byte)value;
			this.sendEvent(event);
		}
	}
	
	public void sendNoteOff(int channel, int key, int velocity) throws MidiPlayerException {
		if( this.connection.isConnected()){
			byte[] event = new byte[3];
			event[0] = (byte)(0x80 | channel );
			event[1] = (byte)key;
			event[2] = (byte)velocity;
			this.sendEvent(event);
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity) throws MidiPlayerException {
		if( this.connection.isConnected()){
			byte[] event = new byte[3];
			event[0] = (byte)(0x90 | channel );
			event[1] = (byte)key;
			event[2] = (byte)velocity;
			this.sendEvent(event);
		}
	}
	
	public void sendPitchBend(int channel, int value) throws MidiPlayerException {
		if( this.connection.isConnected()){
			byte[] event = new byte[3];
			event[0] = (byte)(0xE0 | channel );
			event[1] = (byte)0;
			event[2] = (byte)value;
			this.sendEvent(event);
		}
	}
	
	public void sendProgramChange(int channel, int value) throws MidiPlayerException {
		if( this.connection.isConnected()){
			byte[] event = new byte[2];
			event[0] = (byte)(0xC0 | channel );
			event[1] = (byte)value;
			this.sendEvent(event);
		}
	}
	
	public void sendSystemReset() {
		//not implemented
	}

	public void sendEvent(final byte[] event) throws MidiPlayerException {
		try {
			if( this.connection.isConnected() ) {
				this.connection.getMidiInputPort().send(event, 0, event.length);
			}
		} catch (IOException e) {
			throw new MidiPlayerException(e);
		}
	}
}
