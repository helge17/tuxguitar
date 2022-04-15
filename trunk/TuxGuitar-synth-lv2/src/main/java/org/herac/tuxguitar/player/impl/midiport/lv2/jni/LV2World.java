package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

import java.util.ArrayList;
import java.util.List;

public class LV2World extends LV2Object {
	
	private List<LV2Plugin> plugins;
	
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
		if( this.plugins == null && this.isInitialized()) {
			this.plugins = this.getAllPlugins(this.getInstance());
		}
		return (this.plugins != null ? this.plugins : new ArrayList<LV2Plugin>());
	}
	
	private native long malloc();
	
	private native void free(long instance);
	
	private native List<LV2Plugin> getAllPlugins(long instance);
}
