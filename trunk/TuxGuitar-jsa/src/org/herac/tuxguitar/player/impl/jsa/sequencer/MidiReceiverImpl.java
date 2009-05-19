package org.herac.tuxguitar.player.impl.jsa.sequencer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.herac.tuxguitar.player.base.MidiPlayerException;

public class MidiReceiverImpl implements Receiver{
	
	private MidiSequencerImpl sequencer;
	
	public MidiReceiverImpl(MidiSequencerImpl sequencer){
		this.sequencer = sequencer;
	}
	
	public void send(MidiMessage message, long timeStamp) {
		try {
			if( this.sequencer.isRunning() ){
				parseMessage(message.getMessage());
			}
		} catch (MidiPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		//not implemented
	}
	
	private void parseMessage(byte[] data) throws MidiPlayerException{
		int length = data.length;
		
		//NOTE ON
		if((((length > 0)?(data[0] & 0xFF):0) & 0xF0) == ShortMessage.NOTE_ON){
			parseNoteOn(data);
		}
		//NOTE OFF
		else if((((length > 0)?(data[0] & 0xFF):0) & 0xF0) == ShortMessage.NOTE_OFF){
			parseNoteOff(data);
		}
		//PROGRAM CHANGE
		else if((((length > 0)?(data[0] & 0xFF):0) & 0xF0) == ShortMessage.PROGRAM_CHANGE){
			parseProgramChange(data);
		}
		//CONTROL CHANGE
		else if((((length > 0)?(data[0] & 0xFF):0) & 0xF0) == ShortMessage.CONTROL_CHANGE){
			parseControlChange(data);
		}
		//PITCH BEND
		else if((((length > 0)?(data[0] & 0xFF):0) & 0xF0) == ShortMessage.PITCH_BEND){
			parsePitchBend(data);
		}
	}
	
	private void parseNoteOn(byte[] data) throws MidiPlayerException{
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):0;
		int value = (length > 1)?(data[1] & 0xFF):0;
		int velocity = (length > 2)?(data[2] & 0xFF):0;
		
		if(velocity == 0){
			parseNoteOff(data);
		}else if(value > 0){
			this.sequencer.getTransmitter().sendNoteOn(channel,value,velocity);
		}
	}
	
	private void parseNoteOff(byte[] data) throws MidiPlayerException{
		int length = data.length;
		
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):0;
		int value = (length > 1)?(data[1] & 0xFF):0;
		int velocity = (length > 2)?(data[2] & 0xFF):0;
		
		this.sequencer.getTransmitter().sendNoteOff(channel,value,velocity);
	}
	
	private void parseProgramChange(byte[] data) throws MidiPlayerException{
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):-1;
		int instrument = (length > 1)?(data[1] & 0xFF):-1;
		if(channel != -1 && instrument != -1){
			this.sequencer.getTransmitter().sendProgramChange(channel,instrument);
		}
	}
	
	private void parseControlChange(byte[] data) throws MidiPlayerException{
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):-1;
		int control = (length > 1)?(data[1] & 0xFF):-1;
		int value = (length > 2)?(data[2] & 0xFF):-1;
		if(channel != -1 && control != -1 && value != -1){
			this.sequencer.getTransmitter().sendControlChange(channel,control,value);
		}
	}
	
	private void parsePitchBend(byte[] data) throws MidiPlayerException{
		int length = data.length;
		int channel = (length > 0)?((data[0] & 0xFF) & 0x0F):-1;
		int value = (length > 2)?(data[2] & 0xFF):-1;
		if(channel != -1 && value != -1){
			this.sequencer.getTransmitter().sendPitchBend(channel,value);
		}
	}
}
