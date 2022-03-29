//package org.herac.tuxguitar.player.impl.midiport.lv2.jni;
//
//public class LV2Instance extends LV2Object {
//	
//	public LV2Instance(LV2Plugin plugin, int bufferSize) {
//		this.setInstance(malloc(plugin.getInstance(), bufferSize));
//	}
//	
//	public void finalize() {
//		if( this.isInitialized()) {
//			this.free(this.getInstance());
//			this.setInstance(0);
//		}
//	}
//	
//	public void setControlPortValue(int index, float value) {
//		if( this.isInitialized()) {
//			this.setControlPortValue(this.getInstance(), index, value);
//		}
//	}
//	
//	public float getControlPortValue(int index) {
//		if( this.isInitialized()) {
//			return this.getControlPortValue(this.getInstance(), index);
//		}
//		return 0f;
//	}
//	
//	public void setMidiMessages(Object[] messages) {
//		if( this.isInitialized()) {
//			this.setMidiMessages(this.getInstance(), messages);
//		}
//	}
//	
//	public void processAudio(float[][] inputs, float[][] outputs) {
//		if( this.isInitialized()) {
//			this.processAudio(this.getInstance(), inputs, outputs);
//		}
//	}
//	
//	private native long malloc(long plugin, int bufferSize);
//	
//	private native void free(long instance);
//	
//	private native void setControlPortValue(long instance, int index, float value);
//	
//	private native float getControlPortValue(long instance, int index);
//	
//	private native void setMidiMessages(long instance, Object[] messages);
//	
//	private native void processAudio(long instance, float[][] inputs, float[][] outputs);
//}
