package org.herac.tuxguitar.android.midi.port.gervill;

import javax.sound.midi.MidiChannel;

import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiControllers;

public class MidiReceiverImpl implements GMReceiver{
	
	private MidiSynthesizerManager synthManager;
	
	public MidiReceiverImpl(MidiSynthesizerManager synthManager){
		this.synthManager = synthManager;
	}
	
	public void sendNoteOn(int channel, int key, int velocity){
		MidiChannel midiChannel = this.synthManager.getChannel(channel);
		if( midiChannel != null ){
			midiChannel.noteOn(key, velocity);
		}
	}
	
	public void sendNoteOff(int channel, int key, int velocity){
		MidiChannel midiChannel = this.synthManager.getChannel(channel);
		if( midiChannel != null ){
			midiChannel.noteOff(key, velocity);
		}
	}
	
	public void sendPitchBend(int channel, int value){
		MidiChannel midiChannel = this.synthManager.getChannel(channel);
		if( midiChannel != null ){
			midiChannel.setPitchBend( (value * 128) );
		}
	}
	
	public void sendControlChange(int channel, int controller, int value) {
		MidiChannel midiChannel = this.synthManager.getChannel(channel);
		if( midiChannel != null ){
			if( MidiControllers.BANK_SELECT == controller ) {
				this.synthManager.loadInstrument(this.synthManager.toPatch(value, this.synthManager.findCurrentProgram(channel)));
			}
			midiChannel.controlChange(controller, value);
			
			if( MidiControllers.BANK_SELECT == controller ) {
				this.synthManager.unloadOrphanInstruments();
			}
		}
	}
	
	public void sendProgramChange(int channel, int value){
		MidiChannel midiChannel = this.synthManager.getChannel(channel);
		if( midiChannel != null ){
			this.synthManager.loadInstrument(this.synthManager.toPatch(this.synthManager.findCurrentBank(channel), value));
			
			midiChannel.programChange(value);
			
			this.synthManager.unloadOrphanInstruments();
		}
	}
	
	public void sendSystemReset(){
		if( this.synthManager.getChannels() != null ){
			for(MidiChannel midiChannel : this.synthManager.getChannels()){
				midiChannel.resetAllControllers();
			}
		}
	}
	
	public void sendAllNotesOff(){
		if( this.synthManager.getChannels() != null ){
			for(int channel = 0; channel < this.synthManager.getChannels().length; channel ++){
				this.sendControlChange(channel, MidiControllers.ALL_NOTES_OFF, 0);
			}
		}
	}
}