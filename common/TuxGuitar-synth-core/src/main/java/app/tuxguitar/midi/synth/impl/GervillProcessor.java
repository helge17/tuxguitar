package app.tuxguitar.midi.synth.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.util.TGContext;

import media.sound.AudioSynthesizer;
import media.sound.SoftSynthesizer;

public class GervillProcessor implements TGMidiProcessor {

	private static final String SYNTH_PROGRAM_PARAM = "gervil.program";
	private static final String SYNTH_CHANNEL_MODE_PARAM = "gervil.channel.mode";
	private static final String SYNTH_SOUNDBANK_PATH_PARAM = "gervil.soundbank.path";

	private static final String SYNTH_LOAD_DEFAULT_SOUNDBANK_PARAM = "load default soundbank";
	private static final String SYNTH_MIDI_CHANNELS_PARAM = "midi channels";
	private static final String SYNTH_CONTROL_RATE_PARAM = "control rate";

	// Gervill renders a control period at a time, this many per second (its default)
	private static final float CONTROL_RATE = 147f;

	private TGContext context;
	private AudioSynthesizer synth;
	private AudioInputStream stream;
	private Receiver receiver;
	// what it plays: replaced, never changed in place, so an instrument that loaded for an older one is dropped
	private volatile GervillProgram program;
	// the channel's bank and program it follows (see setProgram); none until told
	private int followedBank;
	private int followedProgram = -1;
	private GervillSoundbankFactory soundbankFactory;
	private byte[] buffer;
	private float[][] outputs;
	private AudioFormat format;
	// the frames read from the stream since it opened
	private long frames;

	public GervillProcessor(TGContext context) {
		this.context = context;
		this.synth = new SoftSynthesizer();
		this.program = new GervillProgram();
		this.soundbankFactory = new GervillSoundbankFactory();
		this.buffer = new byte[TGAudioBuffer.CHANNELS * TGAudioBuffer.BUFFER_SIZE];
		this.outputs = new float[TGAudioBuffer.CHANNELS][TGAudioBuffer.BUFFER_SIZE / 2];
		for(int i = 0; i < this.outputs.length; i++) {
			this.outputs[i] = new float[TGAudioBuffer.BUFFER_SIZE / 2];
		}
		this.program.setProgram(-1);
		this.format = TGAudioBuffer.AUDIO_FORMAT;
	}

	public void close() {
		if( this.receiver != null ){
			this.receiver.close();
		}
		if( this.synth != null && this.synth.isOpen() ){
			this.synth.close();
		}
		this.stream = null;
		this.receiver = null;
		this.synth = null;
		this.buffer = null;
	}

	public boolean isOpen() {
		return (this.buffer != null );
	}

	public boolean isBusy() {
		return (this.isOpen() && this.soundbankFactory.isBusy());
	}

	public void finalize() {
		this.close();
	}

	public void fillBuffer(TGAudioBuffer buffer) {
		try {
			if( this.stream != null ) {
				this.frames += this.stream.read(buffer.getBuffer()) / this.format.getFrameSize();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setSampleRate(float sampleRate) {
		this.format = new AudioFormat(sampleRate, 16, TGAudioBuffer.CHANNELS, true, TGAudioBuffer.BIGENDIAN);
	}

	public void loadProgram(GervillProgram program) {
		if(!this.program.equals(program)) {
			this.program = copy(program);
			this.loadInstrument();
		}
	}

	private static GervillProgram copy(GervillProgram program) {
		GervillProgram copy = new GervillProgram();
		copy.copyFrom(program);
		return copy;
	}

	// plays the channel's bank and program with the program's soundbank and channel mode from now on, whatever program
	// its parameters name. Once open, only the instrument changes: the stream, and the controls sent to it, are kept
	public void setProgram(int bank, int program) {
		this.followedBank = bank;
		this.followedProgram = program;
		GervillProgram followed = this.followed(this.program);
		if( this.stream == null ) {
			this.loadProgram(followed);
		} else if(!this.program.equals(followed)) {
			this.program = followed;
			this.requestInstrument();
		}
	}

	// the program with the bank and program followed, if any
	private GervillProgram followed(GervillProgram program) {
		GervillProgram followed = copy(program);
		if( this.followedProgram >= 0 ) {
			followed.setBank(this.followedBank);
			followed.setProgram(this.followedProgram);
		}
		return followed;
	}

	public void loadInstrument() {
		try {
			if( this.synth.isOpen()) {
				this.synth.close();
			}
			this.stream = this.synth.openStream(this.format, this.createSynthInfo());
			this.frames = 0;
			this.receiver = this.synth.getReceiver();

			this.requestInstrument();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// the program's instrument loads on a thread of its own; one that loaded for a program no longer played is dropped
	private void requestInstrument() {
		GervillProgram requested = this.program;
		this.soundbankFactory.create(this.context, requested, instrument -> this.instrumentLoaded(requested, instrument));
	}

	void instrumentLoaded(GervillProgram requested, Instrument instrument) {
		if( requested == this.program ) {
			this.loadInstrument(instrument);
		}
	}

	public void loadInstrument(Instrument instrument) {
		this.synth.loadInstrument(instrument);

		Patch patch = instrument.getPatch();
		for(MidiChannel midiChannel : this.synth.getChannels()) {
			midiChannel.programChange(patch.getBank(), patch.getProgram());
		}
	}

	public Map<String, Object> createSynthInfo(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SYNTH_LOAD_DEFAULT_SOUNDBANK_PARAM, false);
		map.put(SYNTH_CONTROL_RATE_PARAM, CONTROL_RATE);
		if( this.program.getChannelMode() == GervillProgram.CHANNEL_MODE_SINGLE ) {
			map.put(SYNTH_MIDI_CHANNELS_PARAM, 1);
		}
		else if( this.program.getChannelMode() == GervillProgram.CHANNEL_MODE_BEND ) {
			map.put(SYNTH_MIDI_CHANNELS_PARAM, 2);
		}
		else if( this.program.getChannelMode() == GervillProgram.CHANNEL_MODE_VOICE ) {
			map.put(SYNTH_MIDI_CHANNELS_PARAM, 16);
		}
		return map;
	}

	public void storeParameters(Map<String, String> parameters) {
		parameters.put(SYNTH_PROGRAM_PARAM, this.program.getBank() + ":" + this.program.getProgram());
		parameters.put(SYNTH_CHANNEL_MODE_PARAM, Integer.toString(this.program.getChannelMode()));
		parameters.put(SYNTH_SOUNDBANK_PATH_PARAM, this.program.getSoundbankPath());
	}

	public void restoreParameters(Map<String, String> parameters) {
		try {
			GervillProgram program = new GervillProgram();

			String programParam = parameters.get(SYNTH_PROGRAM_PARAM);
			String channelModeParam = parameters.get(SYNTH_CHANNEL_MODE_PARAM);
			String soundbankPathParam = parameters.get(SYNTH_SOUNDBANK_PATH_PARAM);

			if( programParam != null ) {
				String[] parts = programParam.trim().split(":");
				if( parts.length == 2 ) {
					program.setBank(Integer.valueOf(parts[0].trim()));
					program.setProgram(Integer.valueOf(parts[1].trim()));
				}
			}
			if( channelModeParam != null ) {
				program.setChannelMode(Integer.valueOf(channelModeParam.trim()));
			}
			program.setSoundbankPath(soundbankPathParam);

			this.loadProgram(this.followed(program));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) {
		this.synth.getChannels()[resolveChannel(voice, bendMode)].noteOn(key, velocity);
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) {
		this.synth.getChannels()[resolveChannel(voice, bendMode)].noteOff(key, velocity);
	}

	// a timed event reaches the instrument on a frame of the next fill (counted from its first frame), as Gervill renders
	// it: it is heard latency(rate) frames later in the stream. Gervill has rendered less than was read (its stream lags
	// it), so an event sent before a fill is never late. A note starts on its frame; other events start the control
	// period that holds it.

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode, long frame) {
		this.sendAt(ShortMessage.NOTE_ON, this.resolveChannel(voice, bendMode), key, velocity, frame);
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode, long frame) {
		this.sendAt(ShortMessage.NOTE_OFF, this.resolveChannel(voice, bendMode), key, velocity, frame);
	}

	// the bend's 7 bits are the high ones of MIDI's 14
	public void sendPitchBend(int value, int voice, boolean bendMode, long frame) {
		this.sendAt(ShortMessage.PITCH_BEND, this.resolveChannel(voice, bendMode), 0, value, frame);
	}

	public void sendControlChange(int controller, int value, long frame) {
		for(int channel = 0; channel < this.synth.getChannels().length; channel++) {
			this.sendAt(ShortMessage.CONTROL_CHANGE, channel, controller, value, frame);
		}
	}

	// Gervill times a message in microseconds of what it rendered, and starts a note on the frame nearest to it
	private void sendAt(int command, int channel, int data1, int data2, long frame) {
		try {
			this.receiver.send(new ShortMessage(command, channel, data1, data2), Math.round((this.frames + frame) * 1000000.0 / this.format.getSampleRate()));
		} catch (InvalidMidiDataException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// the frames from where Gervill renders an event to where its stream sounds it: two control periods, one of silence
	// the stream opens with and one its limiter holds back to look ahead
	public static int latency(float sampleRate) {
		return 2 * (int) (sampleRate / CONTROL_RATE);
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) {
		this.synth.getChannels()[resolveChannel(voice, bendMode)].setPitchBend(value * 128);
	}

	public void sendControlChange(int controller, int value) {
		for(MidiChannel midiChannel : this.synth.getChannels()) {
			midiChannel.controlChange(controller, value);
		}
	}

	public int resolveChannel(int voice, boolean bendMode) {
		if( this.program.getChannelMode() == GervillProgram.CHANNEL_MODE_BEND ) {
			return (bendMode ? 1 : 0);
		}
		if( this.program.getChannelMode() == GervillProgram.CHANNEL_MODE_VOICE ) {
			return (voice >= 0 && voice < 9 ? voice : 10);
		}
		return 0;
	}

	public GervillProgram getProgram() {
		return this.program;
	}
}
