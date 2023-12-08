package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;

public class MidiTempoInUsqMessage extends MetaMessage{
	
	public MidiTempoInUsqMessage(int usq) throws InvalidMidiDataException{
		this.setMessage(0x51, new byte[]{ (byte)((usq >> 16) & 0x00FF),(byte)((usq >> 8) & 0x00FF),(byte)((usq) & 0x00FF) }, 3);
	}
}
