package org.herac.tuxguitar.player.impl.midiport.vst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sound.midi.ShortMessage;

import org.herac.tuxguitar.midi.synth.TGAudioBuffer;
import org.herac.tuxguitar.player.impl.midiport.vst.jni.VSTEffect;
import org.herac.tuxguitar.player.impl.midiport.vst.jni.VSTEffectUI;
import org.herac.tuxguitar.player.impl.midiport.vst.jni.VSTPlugin;

public class VSTEffectProcessor {
	
	public static final int BUFFER_SIZE = ( TGAudioBuffer.BUFFER_SIZE / 2 ) ;
	public static final float SAMPLE_RATE = ( TGAudioBuffer.SAMPLE_RATE );
	
	private Object lock = new Object();
	
	private VSTPlugin plugin;
	private VSTEffect effect;
	private VSTEffectUI effectUI;
	private List<byte[]> messages;
	private float[][] inputs;
	private float[][] outputs;
	private boolean open;
	
	public VSTEffectProcessor(VSTPlugin plugin) {
		this.plugin = plugin;
		this.effect = new VSTEffect(plugin);
		this.effectUI = new VSTEffectUI(this.effect);
		this.open();
	}
	
	public void finalize(){
		synchronized (this.lock) {
			this.close();
			this.effectUI.finalize();
			this.effect.finalize();
		}
	}
	
	public void open(){
		synchronized (this.lock) {
			if(!this.isOpen()){
				this.effect.open();
				this.effect.setBlockSize(BUFFER_SIZE);
				this.effect.setSampleRate(SAMPLE_RATE);
				this.messages = new ArrayList<byte[]>();
				this.inputs = new float[this.effect.getNumInputs()][BUFFER_SIZE];
				this.outputs = new float[this.effect.getNumOutputs()][BUFFER_SIZE];
				this.open = true;
			}
		}
	}
	
	public void close(){
		synchronized (this.lock) {
			if( this.isOpen()){
				this.open = false;
				this.effectUI.closeNativeEditor();
				this.effect.close();
			}
		}
	}
	
	public boolean isOpen(){
		return (this.open);
	}
	
	public void queueMidiMessage(ShortMessage message){
		synchronized (this.lock) {
			if( this.isOpen()){
				byte[] pack = new byte[]{
					(byte)message.getCommand(), (byte)message.getChannel(), (byte)message.getData1(), (byte)message.getData2()
				};
				this.messages.add( pack );
			}
		}
	}
	
	public void fillBuffer(TGAudioBuffer buffer) {
		synchronized (this.lock) {
			if( this.isOpen()){
				this.processMessages();
				this.processReplacing(buffer);
			}
		}
	}
	
	public void processMessages(){
		synchronized (this.lock) {
			if( this.isOpen()){
				if(!this.messages.isEmpty() ){
					this.effect.sendMessages(this.messages.toArray());
					this.messages.clear();
				}
			}
		}
	}
	
	public void processReplacing(TGAudioBuffer buffer){
		synchronized (this.lock) {
			if( this.isOpen() ) {
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

	public VSTPlugin getPlugin() {
		return plugin;
	}

	public VSTEffect getEffect() {
		return effect;
	}
	
	public VSTEffectUI getEffectUI() {
		return effectUI;
	}
	
	public void restoreParameters(Map<String, String> parameters) {
		// todo
	}
	
	public void storeParameters(Map<String, String> parameters) {
		// todo
	}
}
