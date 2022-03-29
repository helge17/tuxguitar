package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

import java.util.ArrayList;
import java.util.Collections;
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
		List<LV2Plugin> plugins = null;//new ArrayList<LV2Plugin>();
		if( this.isInitialized()) {
			plugins = this.getAllPlugins(this.getInstance());
			
			/*
			List<Long> lilvPlugins = this.getLilvPlugins(this.getInstance());
			if( lilvPlugins != null ) {
				for(Long lilvPlugin : lilvPlugins) {
					plugins.add(new LV2Plugin(this, lilvPlugin));
				}
			}
			*/
		}
		return (plugins != null ? plugins : new ArrayList<LV2Plugin>());
	}
	
	private native long malloc();
	
	private native void free(long instance);
	
	private native List<LV2Plugin> getAllPlugins(long instance);
}
