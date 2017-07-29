package org.herac.tuxguitar.midi.synth;

import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiParameters;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public class TGSynthChannel implements MidiChannel {
	
	public static final String CUSTOM_PROGRAM_PREFIX = "synth.program";
	
	private int id;
	private int midiBank;
	private int midiProgram;
	private TGSynthesizer synthesizer;
	private TGSynthChannelProcessor processor;
	private TGSynthChannelProperties parameters;
	private TGProgram program;
	private boolean customProgram;
	
	public TGSynthChannel(TGSynthesizer synthesizer, int id){
		this.synthesizer = synthesizer;
		this.id = id;
		this.midiBank = 0;
		this.midiProgram = -1;
		this.program = new TGProgram();
		this.parameters = new TGSynthChannelProperties();
	}
	
	public int getId(){
		return this.id;
	}
	
	public TGProgram getProgram() {
		return this.program;
	}
	
	public void loadProgram(TGProgram program) {
		this.program.copyFrom(program);
		this.openProcessor();
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
		}
	}
	
	public void fillBuffer(TGAudioBuffer buffer){
		if( this.processor != null){
			this.processor.fillBuffer(buffer);
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
			}else{
				if( this.processor != null && this.processor.getProcessor() != null ) {
					this.processor.getProcessor().sendControlChange(controller, value);
				}
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
}
