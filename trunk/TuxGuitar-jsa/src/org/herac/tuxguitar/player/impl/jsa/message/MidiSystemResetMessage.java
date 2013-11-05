package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class MidiSystemResetMessage extends MidiShortMessage{
	
	public MidiSystemResetMessage() throws InvalidMidiDataException{
		this.setChannel(0);
		this.setMessage(ShortMessage.SYSTEM_RESET);
		this.setVoice(MidiShortMessage.DEFAULT_VOICE);
		this.setBendMode(MidiShortMessage.DEFAULT_BEND_MODE);
	}
}
