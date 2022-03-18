package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.util.TGContext;

public class VSTAudioProcessor implements TGAudioProcessor {
	
	public static final int BUFFER_SIZE = ( TGAudioBuffer.BUFFER_SIZE / 2) ;
	public static final float SAMPLE_RATE = ( TGAudioBuffer.SAMPLE_RATE );
	public static final String PARAM_FILE_NAME = "vst.filename";
	public static final String PARAM_CHUNK = "vst.chunk";
	public static final String PARAM_PREFIX = "vst.param.";
	
	private Object lock = new Object();
	private TGContext context;
	private VSTEffect effect;
	private File file;
	private List<byte[]> messages;
	private float[][] inputs;
	private float[][] outputs;
	private Map<String, String> appliedParameters;
	
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
					this.effect.setActive(true);
					this.effect.startProcess();
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
				this.effect.stopProcess();
				this.effect.setActive(false);
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
		if( this.isOpen() ) {
			if( this.appliedParameters == null || !this.appliedParameters.equals(parameters)) {
				this.appliedParameters = new HashMap<>(parameters);
				
				int version = this.getEffect().getVersion();
				if( version <= 2300 ) {
					this.getEffect().setActive(false);
				}
				
				this.getEffect().beginSetProgram();
				
				if( parameters.containsKey(PARAM_CHUNK)) {
					String chunkData = parameters.get(PARAM_CHUNK);
					if( chunkData != null && chunkData.length() > 0 ) {
						this.getEffect().setChunk(Base64.getDecoder().decode(chunkData));
					}
				}
				
				int paramCount = this.getEffect().getNumParams();
				if( paramCount > 0 ) {
					for(int i = 0 ; i < paramCount ; i ++) {
						String key = PARAM_PREFIX + i;
						if( parameters.containsKey(key)) {
							this.getEffect().setParameter(i, Float.parseFloat(parameters.get(key)));
						}
					}
				}
				
				this.getEffect().endSetProgram();
				
				if( version <= 2300 ) {
					this.getEffect().setActive(true);
				}
				
				this.fireParamsEvent(VSTParamsEvent.ACTION_RESTORE, parameters);
			}
		}
	}
	
	public void storeParameters(Map<String, String> parameters) {
		if( this.isOpen() ) {
			parameters.put(PARAM_FILE_NAME, this.file.getAbsolutePath());
			
			int paramCount = this.getEffect().getNumParams();
			for(int i = 0 ; i < paramCount ; i ++) {
				parameters.put(PARAM_PREFIX + i, Float.toString(this.getEffect().getParameter(i)));
			}
			
			byte[] chunk = this.getEffect().getChunk();
			
			parameters.put(PARAM_CHUNK, (chunk != null ? new String(Base64.getEncoder().encode(chunk)) : null));
			
			this.appliedParameters = new HashMap<>(parameters);
			
			this.fireParamsEvent(VSTParamsEvent.ACTION_STORE, parameters);
		}
	}
	
	public void fireParamsEvent(Integer action, Map<String, String> parameters) {
		if( this.isOpen() ) {
			TGEventManager.getInstance(this.context).fireEvent(new VSTParamsEvent(this.getEffect().getSession(), action, parameters));
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
	
	public boolean isBusy() {
		return false;
	}
}
