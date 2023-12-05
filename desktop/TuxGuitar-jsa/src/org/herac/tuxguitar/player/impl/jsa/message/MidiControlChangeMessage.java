package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class MidiControlChangeMessage extends MidiShortMessage{
	
	public MidiControlChangeMessage(int channel,int controller,int value) throws InvalidMidiDataException{
		this.setChannel(channel);
		this.setMessage(ShortMessage.CONTROL_CHANGE, controller, value);
		this.setVoice(MidiShortMessage.DEFAULT_VOICE);
		this.setBendMode(MidiShortMessage.DEFAULT_BEND_MODE);
	}
}
