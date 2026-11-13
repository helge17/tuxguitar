package app.tuxguitar.midi.synth;

import java.util.function.Consumer;

import app.tuxguitar.player.base.MidiChannel;
import app.tuxguitar.player.base.MidiControllers;
import app.tuxguitar.player.base.MidiParameters;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGChannel;

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
				this.processor = new TGSynthChannelProcessor(this.synthesizer.getSampleRate());
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

	// what the channel's chain sounds, before Volume and Balance; silence without a program
	public void fillChain(TGAudioBuffer buffer){
		buffer.clear();
		if( this.processor != null){
			this.processor.fillChain(buffer);
		}
	}

	// how many frames later than its events the channel's chain sounds them: the latencies along it
	public int getLatency(){
		return (this.processor != null ? this.processor.getLatency() : 0);
	}

	public void fillBuffer(TGAudioBuffer buffer){
		this.fillBuffer(buffer, 0);
	}

	// what the chain sounds, delayed by the frames (plugin delay compensation), at the channel's Volume and Balance
	public void fillBuffer(TGAudioBuffer buffer, int delay){
		if( this.processor != null){
			this.processor.fillBuffer(buffer, this.volume, this.balance, delay);
		}
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		this.toSource(source -> source.sendNoteOn(key, velocity, voice, bendMode));
	}

	// the timed events reach the source on a frame of the block about to be rendered
	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode, int frame) throws MidiPlayerException {
		this.toSource(source -> source.sendNoteOn(key, velocity, voice, bendMode, frame));
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		this.toSource(source -> source.sendNoteOff(key, velocity, voice, bendMode));
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode, int frame) throws MidiPlayerException {
		this.toSource(source -> source.sendNoteOff(key, velocity, voice, bendMode, frame));
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) throws MidiPlayerException {
		this.sendPitchBend(value, voice, bendMode, source -> source.sendPitchBend(value, voice, bendMode));
	}

	public void sendPitchBend(int value, int voice, boolean bendMode, int frame) throws MidiPlayerException {
		this.sendPitchBend(value, voice, bendMode, source -> source.sendPitchBend(value, voice, bendMode, frame));
	}

	// without a source yet, the bend waits for one, untimed
	private void sendPitchBend(int value, int voice, boolean bendMode, Consumer<TGMidiProcessor> send) throws MidiPlayerException {
		if(!this.toSource(send)) {
			this.pendingEvents.addPitchBend(value, voice, bendMode);
		}
	}

	public void sendControlChange(int controller, int value) throws MidiPlayerException {
		this.sendControlChange(controller, value, source -> source.sendControlChange(controller, value));
	}

	public void sendControlChange(int controller, int value, int frame) throws MidiPlayerException {
		this.sendControlChange(controller, value, source -> source.sendControlChange(controller, value, frame));
	}

	// Bank select, Volume and Balance are the channel's; other controls go to the source, or wait for one, untimed. A
	// bank select silences the channel until the next program change, but a custom program's source follows it
	private void sendControlChange(int controller, int value, Consumer<TGMidiProcessor> send) throws MidiPlayerException {
		try{
			if( controller == MidiControllers.BANK_SELECT ){
				if( this.midiBank != value ){
					this.midiBank = value;
					if( this.customProgram ) {
						this.followProgram();
					} else {
						this.closeProcessor();
					}
				}
			} else if( controller == MidiControllers.VOLUME ){
				this.volume = value;
			} else if( controller == MidiControllers.BALANCE ){
				this.balance = value;
			} else if(!this.toSource(send)) {
				this.pendingEvents.addControlChange(controller, value);
			}
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}

	// whether the channel has a source to send to
	private boolean toSource(Consumer<TGMidiProcessor> send) {
		TGSynthChannelProcessor processor = this.processor;
		TGMidiProcessor source = (processor != null ? processor.getProcessor() : null);
		if( source != null ) {
			send.accept(source);
		}
		return (source != null);
	}

	// a custom program's source follows the channel's Bank and Program; otherwise the program of that bank and program
	// plays
	public void sendProgramChange(int value) throws MidiPlayerException {
		try{
			if( this.midiProgram != value || this.processor == null ){
				this.midiProgram = value;
				if( this.customProgram ) {
					this.followProgram();
				} else {
					this.loadProgram(this.synthesizer.getProgram(this.midiBank, this.midiProgram));
				}
			}
		}catch(Throwable throwable){
			throw new MidiPlayerException(throwable.getMessage(), throwable);
		}
	}

	// the source of a custom program plays the channel's Bank and Program, once a program change gave one
	private void followProgram() {
		if( this.midiProgram >= 0 ) {
			this.toSource(source -> source.setProgram(this.midiBank, this.midiProgram));
		}
	}

	public void sendAllNotesOff() throws MidiPlayerException {
		this.toSource(source -> source.sendControlChange(MidiControllers.ALL_NOTES_OFF, 0));
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
					this.followProgram();
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
