package org.herac.tuxguitar.player.impl.jsa.message;

import java.nio.charset.Charset;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;

public class MidiTrackNameMessage extends MetaMessage{
	
	public MidiTrackNameMessage(String name) throws InvalidMidiDataException{
		byte[] bytes = name.getBytes(Charset.forName("UTF-8"));
		this.setMessage(0x03, bytes, bytes.length);
	}
}
