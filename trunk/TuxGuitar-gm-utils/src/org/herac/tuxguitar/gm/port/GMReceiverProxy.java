package org.herac.tuxguitar.gm.port;

import org.herac.tuxguitar.player.base.MidiPlayerException;

public class GMReceiverProxy implements GMReceiver {

	private GMReceiver receiver;

	public GMReceiverProxy(GMReceiver receiver) {
		this.receiver = receiver;
	}
	
	public void sendAllNotesOff() throws MidiPlayerException {
		this.receiver.sendAllNotesOff();
	}

	public void sendNoteOn(int channel, int key, int velocity) throws MidiPlayerException {
		if( this.isValidChannel(channel)) {
			this.receiver.sendNoteOn(channel, key, velocity);
		}
	}

	public void sendNoteOff(int channel, int key, int velocity) throws MidiPlayerException {
		if( this.isValidChannel(channel)) {
			this.receiver.sendNoteOff(channel, key, velocity);
		}
	}

	public void sendProgramChange(int channel, int value) throws MidiPlayerException {
		if( this.isValidChannel(channel)) {
			this.receiver.sendProgramChange(channel, value);
		}
	}

	public void sendControlChange(int channel, int controller, int value) throws MidiPlayerException {
		if( this.isValidChannel(channel)) {
			this.receiver.sendControlChange(channel, controller, value);
		}
	}

	public void sendPitchBend(int channel, int value) throws MidiPlayerException {
		if( this.isValidChannel(channel)) {
			this.receiver.sendPitchBend(channel, value);
		}
	}
	
	public boolean isValidChannel(int channel) {
		return (channel >=0 && channel < 16);
	}
}
