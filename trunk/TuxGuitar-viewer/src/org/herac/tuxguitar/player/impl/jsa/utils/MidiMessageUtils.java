package org.herac.tuxguitar.player.impl.jsa.utils;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiMessageUtils {
	
	public static final byte TICK_MOVE = 0x01;
	
	private static int fixValue(int value){
		int fixedValue = value;
		fixedValue = Math.min(fixedValue,127);
		fixedValue = Math.max(fixedValue,0);
		return fixedValue;
	}
	
	private static int fixChannel(int channel){
		int fixedChannel = channel;
		fixedChannel = Math.min(fixedChannel,15);
		fixedChannel = Math.max(fixedChannel,0);
		return fixedChannel;
	}
	
	public static MidiMessage noteOn(int channel,int note,int velocity){
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.NOTE_ON, fixChannel(channel), fixValue(note), fixValue(velocity));
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage noteOff(int channel,int note,int velocity){
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.NOTE_OFF, fixChannel(channel), fixValue(note), fixValue(velocity));
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage controlChange(int channel,int controller,int value){
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.CONTROL_CHANGE,fixChannel(channel),fixValue(controller), fixValue(value));
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage programChange(int channel,int instrument){
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.PROGRAM_CHANGE, fixChannel(channel), fixValue(instrument), 0);
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage pitchBend(int channel,int value){
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.PITCH_BEND, fixChannel(channel), 0, fixValue(value));
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage systemReset(){
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.SYSTEM_RESET);
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage tempoInUSQ(int usq){
		try {
			MetaMessage message = new MetaMessage();
			message.setMessage(0x51, new byte[]{ (byte)((usq >> 16) & 0x00FF),(byte)((usq >> 8) & 0x00FF),(byte)((usq) & 0x00FF) }, 3);
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage timeSignature(TGTimeSignature ts){
		try {
			MetaMessage message = new MetaMessage();
			message.setMessage(0x58, new byte[]{  (byte)ts.getNumerator(),(byte)ts.getDenominator().getIndex(),(byte)(96 / ts.getDenominator().getValue()),8 }, 4);
			return message;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
}