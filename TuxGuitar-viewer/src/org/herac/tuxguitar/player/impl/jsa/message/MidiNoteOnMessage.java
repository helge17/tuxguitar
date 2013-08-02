package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class MidiNoteOnMessage extends MidiShortMessage{
	
	public MidiNoteOnMessage(int channel,int note,int velocity, int voice, boolean bendMode) throws InvalidMidiDataException{
		this.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
		this.setVoice(voice);
		this.setBendMode(bendMode);
	}
	
	public MidiNoteOnMessage(int channel,int note,int velocity) throws InvalidMidiDataException{
		this(channel, note, velocity, DEFAULT_VOICE, DEFAULT_BEND_MODE);
	}
}
