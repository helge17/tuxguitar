package org.herac.tuxguitar.midi.synth;

import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiParameters;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.models.TGChannel;

public class TGSynthChannel implements MidiChannel {
	
	public static final String CUSTOM_PROGRAM_PREFIX = "synth.program";
	
	private int id;
	private int midiBank;
	private int midiProgram;
	private int volume;
	private int balance;
	private TGSynthModel synthesizer;
	private TGSynthChannelProcessor processor;
	private TGSynthChannelProperties parameters;
	private TGSynthChannelPendingQueue pendingEvents;
	private TGProgram program;
	private boolean customProgram;
	
	public TGSynthChannel(TGSynthModel synthesizer, int id){
		this.synthesizer = synthesizer;
		this.id = id;
		this.midiBank = 0;
		this.midiProgram = -1;
		this.volume = TGChannel.DEFAULT_VOLUME;
		this.balance = TGChannel.DEFAULT_BALANCE;
		this.program = new TGProgram();
		this.parameters = new TGSynthChannelProperties();
		this.pendingEvents = new TGSynthChannelPendingQueue(this);
	}
	
	public int getId(){
		return this.id;
	}
	
	public TGProgram getProgram() {
		return this.program;
	}
	
	public void loadProgram(TGProgram program) throws MidiPlayerException {
		this.program.copyFrom(program);
		this.openProcessor();
		this.pendingEvents.dispatch();
	}
	
	public void updateProgram() {
		this.openProcessor();
	}
	
	public TGSynthChannelProcessor getProcessor() {
		return this.processor;
	}
	
	public void openProcessor() {
		if( this.program.getReceiver() != null ) {
			if( this.processor == null ) {
				this.processor = new TGSynthChannelProcessor();
			}
			this.processor.open(this.synthesizer.getContext(), this.program);
		}
	}
	
	public void closeProcessor(){
		if( this.processor != null ){
			this.processor.close();
			this.processor = null;
			this.pendingEvents.clear();
		}
	}
	
	public void fillBuffer(TGAudioBuffer buffer){
		if( this.processor != null){
			this.processor.fillBuffer(buffer, this.volume, this.balance);
		}
	}
	
	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		if( this.processor != null && this.processor.getProcessor() != null ) {
			this.processor.getProcessor().sendNoteOn(key, velocity, voice, bendMode);
		}
	}
	
	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		if( this.processor != null && this.processor.getProcessor() != null ) {
			this.processor.getProcessor().sendNoteOff(key, velocity, voice, bendMode);
		}
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) throws MidiPlayerException {
		if( this.processor != null && this.processor.getProcessor() != null ) {
			this.processor.getProcessor().sendPitchBend(value, voice, bendMode);
		} else {
			this.pendingEvents.addPitchBend(value, voice, bendMode);
		}
	}
	
	public void sendControlChange(int controller, int value) throws MidiPlayerException {
		try{
			if( controller == MidiControllers.BANK_SELECT ){
				if( this.midiBank != value ){
					this.midiBank = value;
					if(!this.customProgram) {
						this.closeProcessor();
					}
				}
			} else if( controller == MidiControllers.VOLUME ){
				this.volume = value;
			} else if( controller == MidiControllers.BALANCE ){
				this.balance = value;
			} else if( this.processor != null && this.processor.getProcessor() != null ) {
				this.processor.getProcessor().sendControlChange(controller, value);
			} else {
				this.pendingEvents.addControlChange(controller, value);
			}
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}
	
	public void sendProgramChange(int value) throws MidiPlayerException {
		try{
			if( this.midiProgram != value || this.processor == null ){
				this.midiProgram = value;
				if(!this.customProgram) {
					this.loadProgram(this.synthesizer.getProgram(this.midiBank, this.midiProgram));
				}
			}
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}
	
	public void sendAllNotesOff() throws MidiPlayerException {		
		if( this.processor != null && this.processor.getProcessor() != null ) {
			this.processor.getProcessor().sendControlChange(MidiControllers.ALL_NOTES_OFF, 0);
		}
	}
	
	public void sendParameter(String key, String value) throws MidiPlayerException {
		if( MidiParameters.SENDING_PARAMS.equals(key) ) {
			if( Boolean.valueOf(value)) {
				this.parameters.clear();
			} else {
				TGProgram program = TGProgramPropertiesUtil.getProgram(this.parameters, CUSTOM_PROGRAM_PREFIX);
				
				this.customProgram = (program != null);
				if( this.customProgram ) {
					this.loadProgram(program);
				} else if(this.midiProgram >= 0){
					this.loadProgram(this.synthesizer.getProgram(this.midiBank, this.midiProgram));
				} else {
					this.closeProcessor();
				}
			}
		} else {
			this.parameters.setValue(key, value);
		}
	}
	
	public boolean isBusy() {
		return (this.processor != null && this.processor.isBusy());
	}
}
