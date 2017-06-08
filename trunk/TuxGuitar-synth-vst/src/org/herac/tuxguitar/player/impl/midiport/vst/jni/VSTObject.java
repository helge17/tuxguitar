package org.herac.tuxguitar.player.impl.midiport.vst.jni;

public class VSTObject {
	
	static {
		System.loadLibrary("tuxguitar-synth-vst-jni");
	}
	
	private long instance;
	
	protected VSTObject(){
		this.instance = 0;
	}
	
	protected long getInstance() {
		return this.instance;
	}
	
	protected void setInstance(long instance) {
		this.instance = instance;
	}
	
	protected boolean isInitialized(){
		return (this.getInstance() != 0);
	}
}
