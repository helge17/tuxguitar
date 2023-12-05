package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class MidiProgramChangeMessage extends MidiShortMessage{
	
	public MidiProgramChangeMessage(int channel,int instrument) throws InvalidMidiDataException{
		this.setChannel(channel);
		this.setMessage(ShortMessage.PROGRAM_CHANGE, instrument, 0);
		this.setVoice(MidiShortMessage.DEFAULT_VOICE);
		this.setBendMode(MidiShortMessage.DEFAULT_BEND_MODE);
	}
}
