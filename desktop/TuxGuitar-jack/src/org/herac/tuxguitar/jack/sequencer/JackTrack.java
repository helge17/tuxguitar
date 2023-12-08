package org.herac.tuxguitar.jack.sequencer;

public class JackTrack {
	
	private boolean solo;
	private boolean mute;
	
	public JackTrack(){
		this.solo = false;
		this.mute = false;
	}
	
	public boolean isMute() {
		return this.mute;
	}
	
	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	public boolean isSolo() {
		return this.solo;
	}
	
	public void setSolo(boolean solo) {
		this.solo = solo;
	}
}
