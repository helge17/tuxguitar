package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.midi.base.MidiMessage;
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
		return MidiMessage.shortMessage(MidiMessage.NOTE_ON, fixChannel(channel), fixValue(note), fixValue(velocity));
	}
	
	public static MidiMessage noteOff(int channel,int note,int velocity){
		return MidiMessage.shortMessage(MidiMessage.NOTE_OFF, fixChannel(channel), fixValue(note), fixValue(velocity));
	}
	
	public static MidiMessage controlChange(int channel,int controller,int value){
		return MidiMessage.shortMessage(MidiMessage.CONTROL_CHANGE, fixChannel(channel), fixValue(controller), fixValue(value));
	}
	
	public static MidiMessage programChange(int channel,int instrument){
		return MidiMessage.shortMessage(MidiMessage.PROGRAM_CHANGE, fixChannel(channel), fixValue(instrument));
	}
	
	public static MidiMessage pitchBend(int channel,int value){
		return MidiMessage.shortMessage(MidiMessage.PITCH_BEND, fixChannel(channel), 0, fixValue(value));
	}
	
	public static MidiMessage systemReset(){
		return MidiMessage.shortMessage(MidiMessage.SYSTEM_RESET);
	}
	
	public static MidiMessage tempoInUSQ(int usq){
		MidiMessage message = new MidiMessage(MidiMessage.TYPE_META, MidiMessage.TEMPO_CHANGE);
		message.setData(new byte[]{(byte)((usq >> 16) & 0xff),(byte)((usq >> 8) & 0xff),(byte)((usq) & 0xff) });
		//message.setData(new byte[]{(byte)((usq >> 16) & 0x00FF),(byte)((usq >> 8) & 0x00FF),(byte)((usq) & 0x00FF) });
		return message;
	}
	
	public static MidiMessage timeSignature(TGTimeSignature ts){
		MidiMessage message = new MidiMessage(MidiMessage.TYPE_META, MidiMessage.TIME_SIGNATURE_CHANGE);
		message.setData(new byte[]{  (byte)ts.getNumerator(),(byte)ts.getDenominator().getIndex(),(byte)(96 / ts.getDenominator().getValue()),8 });
		return message;
	}
	
	public static MidiMessage endOfTrack(){
		return MidiMessage.metaMessage(47,new byte[]{});
	}
}