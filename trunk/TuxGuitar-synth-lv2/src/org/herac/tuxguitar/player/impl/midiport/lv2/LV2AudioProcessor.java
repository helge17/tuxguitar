package org.herac.tuxguitar.player.impl.midiport.lv2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Instance;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;

public class LV2AudioProcessor {
	
	public static final int BUFFER_SIZE = ( TGAudioBuffer.BUFFER_SIZE / 2 ) ;
	public static final float SAMPLE_RATE = ( TGAudioBuffer.SAMPLE_RATE );
	
	private Object lock = new Object();
	
	private LV2Plugin plugin;
	private LV2Instance instance;
	private List<byte[]> messages;
	private float[][] inputs;
	private float[][] outputs;
	private boolean open;
	
	public LV2AudioProcessor(LV2Plugin plugin) {
		this.plugin = plugin;
		this.instance = new LV2Instance(plugin, BUFFER_SIZE);
		this.open();
		
		System.out.println("Instance: " + this.instance);
	}
	
	public void finalize(){
		synchronized (this.lock) {
			this.close();
		}
	}
	
	public void open(){
		synchronized (this.lock) {
			if(!this.isOpen()){
				this.messages = new ArrayList<byte[]>();
				this.inputs = new float[this.instance.getInputPortCount()][BUFFER_SIZE];
				this.outputs = new float[this.instance.getOutputPortCount()][BUFFER_SIZE];
				this.open = true;
			}
		}
	}
	
	public void close(){
		synchronized (this.lock) {
			if( this.isOpen()){
				this.open = false;
			}
		}
	}
	
	public boolean isOpen(){
		return (this.open);
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
//					this.effect.sendMessages(this.messages.toArray());
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
				
				this.instance.processAudio(this.inputs, this.outputs);
				
				if( this.outputs.length > 0 ) {
					buffer.write(this.outputs);
				}
			}
		}
	}

	public LV2Plugin getPlugin() {
		return plugin;
	}

	public LV2Instance getInstance() {
		return instance;
	}
	
	public void restoreParameters(Map<String, String> parameters) {
		// todo
	}
	
	public void storeParameters(Map<String, String> parameters) {
		// todo
	}
}
