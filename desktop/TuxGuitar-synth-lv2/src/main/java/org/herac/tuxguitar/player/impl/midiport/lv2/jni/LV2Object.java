package org.herac.tuxguitar.player.impl.midiport.lv2.jni;

public class LV2Object {
	
	static {
		System.loadLibrary("tuxguitar-synth-lv2-jni");
	}
	
	private long instance;
	
	protected LV2Object() {
		this.instance = 0;
	}
	
	protected long getInstance() {
		return this.instance;
	}
	
	protected void setInstance(long instance) {
		this.instance = instance;
	}
	
	protected boolean isInitialized() {
		return (this.getInstance() != 0);
	}
}
