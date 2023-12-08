package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class MidiNoteOffMessage extends MidiShortMessage{
	
	public MidiNoteOffMessage(int channel,int note,int velocity, int voice, boolean bendMode) throws InvalidMidiDataException{
		this.setChannel(channel);
		this.setMessage(ShortMessage.NOTE_OFF, note, velocity);
		this.setVoice(voice);
		this.setBendMode(bendMode);
	}
}
