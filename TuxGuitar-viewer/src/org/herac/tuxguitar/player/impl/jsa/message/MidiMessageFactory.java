package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import org.herac.tuxguitar.song.models.TGTimeSignature;

public class MidiMessageFactory {
	
	private static int fixValue(int value){
		int fixedValue = value;
		fixedValue = Math.min(fixedValue,127);
		fixedValue = Math.max(fixedValue,0);
		return fixedValue;
	}
	
	public static MidiMessage noteOn(int channel,int note,int velocity, int voice, boolean bendMode){
		try {
			return new MidiNoteOnMessage(channel, fixValue(note), fixValue(velocity), voice, bendMode);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MidiMessage noteOff(int channel,int note,int velocity, int voice, boolean bendMode){
		try {
			return new MidiNoteOffMessage(channel, fixValue(note), fixValue(velocity), voice, bendMode);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage pitchBend(int channel,int value, int voice, boolean bendMode){
		try {
			return new MidiPitchBendMessage(channel, fixValue(value), voice, bendMode);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MidiMessage controlChange(int channel,int controller,int value){
		try {
			return new MidiControlChangeMessage(channel, fixValue(controller), fixValue(value));
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MidiMessage programChange(int channel,int instrument){
		try {
			return new MidiProgramChangeMessage(channel, fixValue(instrument));
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage systemReset(){
		try {
			return new MidiSystemResetMessage();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage tempoInUSQ(int usq){
		try {
			return new MidiTempoInUsqMessage(usq);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage timeSignature(TGTimeSignature tgTimeSignature){
		try {
			return new MidiTimeSignatureMessage(tgTimeSignature);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MidiMessage noteOnGM(int channel,int note,int velocity){
		try {
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setMessage(ShortMessage.NOTE_ON, channel, fixValue(note), fixValue(velocity));
			return shortMessage;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage noteOffGM(int channel,int note,int velocity){
		try {
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setMessage(ShortMessage.NOTE_OFF, channel, fixValue(note), fixValue(velocity));
			return shortMessage;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage pitchBendGM(int channel,int value){
		try {
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setMessage(ShortMessage.PITCH_BEND, channel, 0, fixValue(value));
			return shortMessage;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage controlChangeGM(int channel,int controller,int value){
		try {
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setMessage(ShortMessage.CONTROL_CHANGE,channel, fixValue(controller), fixValue(value));
			return shortMessage;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage programChangeGM(int channel,int instrument){
		try {
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setMessage(ShortMessage.PROGRAM_CHANGE, channel, fixValue(instrument), 0);
			return shortMessage;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidiMessage systemResetGM(){
		try {
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setMessage(ShortMessage.SYSTEM_RESET);
			return shortMessage;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return null;
	}
}