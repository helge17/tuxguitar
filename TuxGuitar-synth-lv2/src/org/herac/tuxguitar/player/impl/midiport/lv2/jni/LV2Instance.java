package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

import java.util.ArrayList;
import java.util.List;

public class LV2Instance extends LV2Object {
	
	public LV2Instance(LV2Plugin plugin, int bufferSize) {
		this.setInstance(malloc(plugin.getInstance(), bufferSize));
	}
	
	public void finalize() {
		if( this.isInitialized()) {
			this.free(this.getInstance());
			this.setInstance(0);
		}
	}
	
	public int getInputPortCount() {
		if( this.isInitialized()) {
			return this.getInputPortCount(this.getInstance());
		}
		return 0;
	}
	
	public int getOutputPortCount() {
		if( this.isInitialized()) {
			return this.getOutputPortCount(this.getInstance());
		}
		return 0;
	}
	
	public void setControlPortValue(int index, float value) {
		if( this.isInitialized()) {
			this.setControlPortValue(this.getInstance(), index, value);
		}
	}
	
	public float getControlPortValue(int index) {
		if( this.isInitialized()) {
			return this.getControlPortValue(this.getInstance(), index);
		}
		return 0f;
	}
	
	public LV2ControlPortInfo getControlPortInfo(int index) {
		if( this.isInitialized()) {
			return this.getControlPortInfo(this.getInstance(), index);
		}
		return null;
	}
	
	public List<Integer> getControlPortIndices() {
		List<Integer> target = new ArrayList<Integer>();
		if( this.isInitialized()) {
			List<Integer> indices = this.getControlPortIndices(this.getInstance());
			if( indices != null ) {
				target.addAll(indices);
			}
		}
		return target;
	}
	
	public void processAudio(float[][] inputs, float[][] outputs) {
		if( this.isInitialized()) {
			this.processAudio(this.getInstance(), inputs, outputs);
		}
	}
	
	private native long malloc(long plugin, int bufferSize);
	
	private native void free(long instance);
	
	private native int getInputPortCount(long instance);
	
	private native int getOutputPortCount(long instance);
	
	private native void setControlPortValue(long instance, int index, float value);
	
	private native float getControlPortValue(long instance, int index);
	
	private native LV2ControlPortInfo getControlPortInfo(long instance, int index);
	
	private native List<Integer> getControlPortIndices(long instance);
	
	private native void processAudio(long instance, float[][] inputs, float[][] outputs);
}
