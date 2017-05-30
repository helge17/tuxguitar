package org.herac.tuxguitar.midi.synth.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioInputStream;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGAudioLine;
import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.util.TGContext;

import com.sun.media.sound.AudioSynthesizer;
import com.sun.media.sound.SoftSynthesizer;

public class GervillProcessor implements TGMidiProcessor {

	private static final String SYNTH_PROGRAM_PARAM = "gervil.program";
	private static final String SYNTH_CHANNEL_MODE_PARAM = "gervil.channel.mode";
	private static final String SYNTH_SOUNDBANK_PATH_PARAM = "gervil.soundbank.path";
	
	private static final String SYNTH_LOAD_DEFAULT_SOUNDBANK_PARAM = "load default soundbank";
	
	private TGContext context;
	private AudioSynthesizer synth;
	private AudioInputStream stream;
	private Receiver receiver;
	private GervillProgram program;
	private byte[] buffer;
	private float[][] outputs;
	
	public GervillProcessor(TGContext context) {
		this.context = context;
		this.program = new GervillProgram();
		this.open();
	}
	
	private Map<String, Object> createDefaultInfo(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SYNTH_LOAD_DEFAULT_SOUNDBANK_PARAM, new Boolean(false));
		return map;
	}
	
	public void open() {
		try {
			this.buffer = new byte[TGAudioLine.CHANNELS * TGAudioLine.BUFFER_SIZE];
			this.outputs = new float[TGAudioLine.CHANNELS][TGAudioLine.BUFFER_SIZE / 2];
			for(int i = 0; i < this.outputs.length; i++) {
				this.outputs[i] = new float[TGAudioLine.BUFFER_SIZE / 2];
			}
			
			this.synth = new SoftSynthesizer();
			this.stream = this.synth.openStream(TGAudioLine.AUDIO_FORMAT, this.createDefaultInfo());
			this.receiver = this.synth.getReceiver();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	public void finalize() {
		this.close();
	}
	
	public void fillBuffer(TGAudioBuffer buffer) {
		try {
			this.stream.read(buffer.getBuffer());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void queueMidiMessage(ShortMessage msg) {
		if( this.receiver != null ) {
			if( this.program.getBank() == 128 ) {
				try {
					msg.setMessage(msg.getCommand(), 9, msg.getData1(), msg.getData2());
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
			
			this.receiver.send(msg, -1);
		}
	}
	
	public void loadProgram(GervillProgram program) {
		if(!this.program.equals(program)) {
			this.program.copyFrom(program);
			this.loadInstrument();
		}
	}
	
	public void loadInstrument() {
		GervillSoundbankFactory gervillSoundbankFactory = new GervillSoundbankFactory();
		gervillSoundbankFactory.create(this.context, this.program, new GervillSoundbankCallback() {
			public void onCreate(Instrument instrument) {
				GervillProcessor.this.loadInstrument(instrument);
			}
		});
	}
	
	public void loadInstrument(Instrument instrument) {
		try {
			this.synth.loadInstrument(instrument);
					
			ShortMessage shortMessage = new ShortMessage();
			shortMessage.setMessage(ShortMessage.PROGRAM_CHANGE, 0, this.program.getProgram(), 0);
			queueMidiMessage(shortMessage);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
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
			
			this.loadProgram(program);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	public GervillProgram getProgram() {
		return this.program;
	}
}
