package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;

import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiTimeSignatureMessage extends MetaMessage{
	
	public MidiTimeSignatureMessage(TGTimeSignature ts) throws InvalidMidiDataException{
		this.setMessage(0x58, new byte[]{  (byte)ts.getNumerator(),(byte)ts.getDenominator().getIndex(),(byte)(96 / ts.getDenominator().getValue()),8 }, 4);
	}
}
