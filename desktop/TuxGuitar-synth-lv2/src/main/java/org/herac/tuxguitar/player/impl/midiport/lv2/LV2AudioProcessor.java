package org.herac.tuxguitar.player.impl.midiport.lv2;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.midi.synth.remote.TGRemoteException;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2RemoteInstance;
import org.herac.tuxguitar.util.TGContext;

public class LV2AudioProcessor {
	
	public static final int BUFFER_SIZE = ( TGAudioBuffer.BUFFER_SIZE / 2 ) ;
	public static final float SAMPLE_RATE = ( TGAudioBuffer.SAMPLE_RATE );
	
	private Object lock = new Object();
	
	private TGContext context;
	private LV2Plugin plugin;
	private LV2RemoteInstance instance;
	private LV2AudioProcessorUpdateCallback updateCallback;
	private List<byte[]> messages;
	private float[][] inputs;
	private float[][] outputs;
	
	public LV2AudioProcessor(TGContext context, LV2Plugin plugin) throws TGRemoteException {
		this.context = context;
		this.plugin = plugin;
		this.open();
	}
	
	public void finalize(){
		synchronized (this.lock) {
			this.close();
		}
	}
	
	public void open() throws TGRemoteException {
		synchronized (this.lock) {
			if(!this.isOpen()){
				this.instance = new LV2RemoteInstance(this.context, this.plugin, BUFFER_SIZE);
				this.messages = new ArrayList<byte[]>();
				this.inputs = new float[this.plugin.getAudioInputPortCount()][BUFFER_SIZE];
				this.outputs = new float[this.plugin.getAudioOutputPortCount()][BUFFER_SIZE];
			}
		}
	}
	
	public void close(){
		synchronized (this.lock) {
			if( this.isOpen()){
				this.instance.closeUI();
				this.instance.close();
				this.instance = null;
			}
		}
	}
	
	public boolean isOpen(){
		return (this.instance != null && !this.instance.isClosed());
	}
	
	public void queueMidiMessage(byte[] midiMessage){
		synchronized (this.lock) {
			if( this.isOpen()){
				this.messages.add(midiMessage);
			}
		}
	}
	
	public void fillBuffer(TGAudioBuffer buffer) {
		synchronized (this.lock) {
			if( this.isOpen()){
				this.processMessages();
				this.processAudio(buffer);
			}
		}
	}
	
	public void processMessages(){
		synchronized (this.lock) {
			if( this.isOpen()){
				if(!this.messages.isEmpty() ){
					this.instance.processMidiMessages(this.messages);
					this.messages.clear();
				}
			}
		}
	}
	
	public void processAudio(TGAudioBuffer buffer){
		synchronized (this.lock) {
			if( this.isOpen() ) {
				if( this.inputs.length > 0 ) {
					buffer.read(this.inputs);
				}
				
				boolean updated = this.instance.processAudio(this.inputs, this.outputs);
				
				if( this.outputs.length > 0 ) {
					buffer.write(this.outputs);
				}
				
				if( this.updateCallback != null && updated ) {
					this.updateCallback.onUpdate();
				}
			}
		}
	}

	public void setUpdateCallback(LV2AudioProcessorUpdateCallback updateCallback) {
		synchronized (this.lock) {
			if( this.isOpen()){
				this.updateCallback = updateCallback;
			}
		}
	}
	
	public LV2Plugin getPlugin() {
		return plugin;
	}

	public LV2RemoteInstance getInstance() {
		return instance;
	}
	
	public interface LV2AudioProcessorUpdateCallback {
		
		void onUpdate();
	}
}
