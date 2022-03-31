package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

import java.util.ArrayList;
import java.util.List;

public class LV2World extends LV2Object {
	
	public LV2World() {
		this.setInstance(malloc());
	}
	
	public void finalize(){
		if( this.isInitialized()) {
			this.free(this.getInstance());
			this.setInstance(0);
		}
	}
	
	public List<LV2Plugin> getPlugins() {
		List<LV2Plugin> plugins = null;
		if( this.isInitialized()) {
			plugins = this.getAllPlugins(this.getInstance());
		}
		return (plugins != null ? plugins : new ArrayList<LV2Plugin>());
	}
	
	private native long malloc();
	
	private native void free(long instance);
	
	private native List<LV2Plugin> getAllPlugins(long instance);
}
