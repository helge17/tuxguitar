package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class MidiPitchBendMessage extends MidiShortMessage{
	
	public MidiPitchBendMessage(int channel,int value, int voice, boolean bendMode) throws InvalidMidiDataException{
		this.setChannel(channel);
		this.setMessage(ShortMessage.PITCH_BEND, 0, value);
		this.setVoice(voice);
		this.setBendMode(bendMode);
	}
}
