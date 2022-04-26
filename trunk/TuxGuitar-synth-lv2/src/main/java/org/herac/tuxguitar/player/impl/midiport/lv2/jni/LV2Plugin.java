package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

import java.util.ArrayList;
import java.util.List;

public class LV2Plugin extends LV2Object {
	
	public LV2Plugin(long instance) {
		this.setInstance(instance);
	}
	
	public LV2Plugin(LV2World world, long lilvPlugin) {
		this.setInstance(malloc(world.getInstance(), lilvPlugin));
	}
	
	public void finalize(){
		if( this.isInitialized()) {
			this.free(this.getInstance());
			this.setInstance(0);
		}
	}
	
	public String getUri(){
		if( this.isInitialized()) {
			return this.getUri(this.getInstance());
		}
		return null;
	}
	
	public String getName(){
		if( this.isInitialized()) {
			return this.getName(this.getInstance());
		}
		return null;
	}

	public String getCategory(){
		if( this.isInitialized()) {
			return this.getCategory(this.getInstance());
		}
		return null;
	}
	
	public int getAudioInputPortCount() {
		if( this.isInitialized()) {
			return this.getAudioInputPortCount(this.getInstance());
		}
		return 0;
	}
	
	public int getAudioOutputPortCount() {
		if( this.isInitialized()) {
			return this.getAudioOutputPortCount(this.getInstance());
		}
		return 0;
	}
	
	public int getMidiInputPortCount() {
		if( this.isInitialized()) {
			return this.getMidiInputPortCount(this.getInstance());
		}
		return 0;
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
	
	private native long malloc(long world, long lilvPlugin);
	
	private native void free(long instance);
	
	private native String getUri(long instance);
	
	private native String getName(long instance);
	
	private native String getCategory(long instance);
	
	private native int getAudioInputPortCount(long instance);
	
	private native int getAudioOutputPortCount(long instance);
	
	private native int getMidiInputPortCount(long instance);
	
	private native LV2ControlPortInfo getControlPortInfo(long instance, int index);
	
	private native List<Integer> getControlPortIndices(long instance);
}
