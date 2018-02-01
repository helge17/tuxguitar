package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.util.TGContext;

public class VSTAudioProcessor implements TGAudioProcessor {
	
	public static final int BUFFER_SIZE = ( TGAudioBuffer.BUFFER_SIZE / 2) ;
	public static final float SAMPLE_RATE = ( TGAudioBuffer.SAMPLE_RATE );
	public static final String PARAM_FILE_NAME = "vst.filename";
	
	private Object lock = new Object();
	private TGContext context;
	private VSTEffect effect;
	private File file;
	private List<byte[]> messages;
	private float[][] inputs;
	private float[][] outputs;
	
	public VSTAudioProcessor(TGContext context) {
		this.context = context;
	}
	
	public void finalize(){
		synchronized (this.lock) {
			if( this.isOpen()) {
				this.close();
			}
		}
	}
	
	public void open(File file){
		synchronized (this.lock) {
			if(!this.isOpen()) {
				try {
					this.file = file;
					this.effect = new VSTEffect(VSTServer.getInstance(this.context).createSession(this.file.getAbsolutePath()));
					this.effect.setBlockSize(BUFFER_SIZE);
					this.effect.setSampleRate(SAMPLE_RATE);
					this.messages = new ArrayList<byte[]>();
					this.inputs = new float[this.effect.getNumInputs()][BUFFER_SIZE];
					this.outputs = new float[this.effect.getNumOutputs()][BUFFER_SIZE];
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void close(){
		synchronized (this.lock) {
			if( this.isOpen()) {
				this.effect.closeNativeEditor();
				this.effect.close();
				this.effect = null;
				this.file = null;
			}
		}
	}
	
	public void queueMidiMessage(byte[] midiMessage){
		synchronized (this.lock) {
			if( this.isOpen()) {
				this.messages.add(midiMessage);
			}
		}
	}
	
	public void fillBuffer(TGAudioBuffer buffer) {
		synchronized (this.lock) {
			if( this.isOpen()) {
				this.processMessages();
				this.processReplacing(buffer);
			}
		}
	}
	
	public void processMessages(){
		synchronized (this.lock) {
			if( this.isOpen()) {
				if(!this.messages.isEmpty() ){
					this.effect.sendMessages(this.messages);
					this.messages.clear();
				}
			}
		}
	}
	
	public void processReplacing(TGAudioBuffer buffer){
		synchronized (this.lock) {
			if( this.isOpen()) {
				if( this.inputs.length > 0 ) {
					buffer.read(this.inputs);
				}
				
				this.effect.sendProcessReplacing(this.inputs, this.outputs, BUFFER_SIZE);
				
				if( this.outputs.length > 0 ) {
					buffer.write(this.outputs);
				}
			}
		}
	}
	
	public void restoreParameters(Map<String, String> parameters) {
		if(!this.isOpen() && parameters.containsKey(PARAM_FILE_NAME)) {
			this.open(new File(parameters.get(PARAM_FILE_NAME)));
		}
	}
	
	public void storeParameters(Map<String, String> parameters) {
		if( this.isOpen() ) {
			parameters.put(PARAM_FILE_NAME, this.file.getAbsolutePath());
		}
	}
	
	public VSTEffect getEffect() {
		return this.effect;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public boolean isOpen() {
		return (this.effect != null && !this.effect.isClosed());
	}
}
