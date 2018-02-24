package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

public class LV2Plugin extends LV2Object {
	
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
	
	private native long malloc(long world, long lilvPlugin);
	
	private native void free(long instance);
	
	private native String getUri(long instance);
	
	private native String getName(long instance);
}
