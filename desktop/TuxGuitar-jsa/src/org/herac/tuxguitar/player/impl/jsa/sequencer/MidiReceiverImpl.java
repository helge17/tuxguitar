package org.herac.tuxguitar.player.impl.jsa.sequencer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.jsa.message.MidiShortMessage;

public class MidiReceiverImpl implements Receiver{
	
	private MidiSequencerImpl sequencer;
	
	public MidiReceiverImpl(MidiSequencerImpl sequencer){
		this.sequencer = sequencer;
	}
	
	public void send(MidiMessage message, long timeStamp) {
		try {
			if( this.sequencer.isRunning() ){
				parseMessage(message);
			}
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		//not implemented
	}
	
	private void parseMessage(MidiMessage message) throws MidiPlayerException{
		byte[] data = message.getMessage();
		if( data.length > 0 ){
			//NOTE ON
			if(((data[0] & 0xFF) & 0xF0) == ShortMessage.NOTE_ON){
				parseNoteOn(data, findChannel(message), findVoice(message), findBendMode(message));
			}
			//NOTE OFF
			else if(((data[0] & 0xFF) & 0xF0) == ShortMessage.NOTE_OFF){
				parseNoteOff(data, findChannel(message), findVoice(message), findBendMode(message));
			}
			//PITCH BEND
			else if(((data[0] & 0xFF) & 0xF0) == ShortMessage.PITCH_BEND){
				parsePitchBend(data, findChannel(message), findVoice(message), findBendMode(message));
			}
			//PROGRAM CHANGE
			else if(((data[0] & 0xFF) & 0xF0) == ShortMessage.PROGRAM_CHANGE){
				parseProgramChange(data, findChannel(message));
			}
			//CONTROL CHANGE
			else if(((data[0] & 0xFF) & 0xF0) == ShortMessage.CONTROL_CHANGE){
				parseControlChange(data, findChannel(message));
			}
		}
	}
	
	private void parseNoteOn(byte[] data, int channel, int voice, boolean bendMode) throws MidiPlayerException{
		int length = data.length;
		int value = (length > 1)?(data[1] & 0xFF):0;
		int velocity = (length > 2)?(data[2] & 0xFF):0;
		
		if( channel >= 0 ){
			if( velocity == 0 ){
				this.parseNoteOff(data, channel, voice, bendMode);
			}else if(value > 0){
				this.sequencer.getTransmitter().sendNoteOn(channel,value,velocity,voice,bendMode);
			}
		}
	}
	
	private void parseNoteOff(byte[] data, int channel, int voice, boolean bendMode) throws MidiPlayerException{
		int length = data.length;
		int value = (length > 1)?(data[1] & 0xFF):0;
		int velocity = (length > 2)?(data[2] & 0xFF):0;
		if( channel >= 0 ){
			this.sequencer.getTransmitter().sendNoteOff(channel,value,velocity,voice,bendMode);
		}
	}
	
	private void parsePitchBend(byte[] data, int channel, int voice, boolean bendMode) throws MidiPlayerException{
		int length = data.length;
		int value = (length > 2)?(data[2] & 0xFF):-1;
		if(channel != -1 && value != -1){
			this.sequencer.getTransmitter().sendPitchBend(channel,value,voice,bendMode);
		}
	}
	
	private void parseProgramChange(byte[] data, int channel) throws MidiPlayerException{
		int length = data.length;
		int instrument = (length > 1)?(data[1] & 0xFF):-1;
		if(channel != -1 && instrument != -1){
			this.sequencer.getTransmitter().sendProgramChange(channel,instrument);
		}
	}
	
	private void parseControlChange(byte[] data, int channel) throws MidiPlayerException{
		int length = data.length;
		int control = (length > 1)?(data[1] & 0xFF):-1;
		int value = (length > 2)?(data[2] & 0xFF):-1;
		if(channel != -1 && control != -1 && value != -1){
			this.sequencer.getTransmitter().sendControlChange(channel,control,value);
		}
	}
	
	private int findChannel(MidiMessage midiMessage){
		if( midiMessage instanceof MidiShortMessage ){
			return ((MidiShortMessage)midiMessage).getChannel();
		}
		byte[] data = midiMessage.getMessage();
		if( data != null && data.length > 0){
			return ((data[0] & 0xFF) & 0x0F);
		}
		return -1;
	}
	
	private int findVoice(MidiMessage midiMessage){
		if( midiMessage instanceof MidiShortMessage ){
			return ((MidiShortMessage)midiMessage).getVoice();
		}
		return MidiShortMessage.DEFAULT_VOICE;
	}
	
	private boolean findBendMode(MidiMessage midiMessage){
		if( midiMessage instanceof MidiShortMessage ){
			return ((MidiShortMessage)midiMessage).isBendMode();
		}
		return MidiShortMessage.DEFAULT_BEND_MODE;
	}
}
