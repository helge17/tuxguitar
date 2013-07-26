package org.herac.tuxguitar.player.base;

public interface MidiPlayerListener {
	
	public void notifyStarted();
	
	public void notifyStopped();
	
	public void notifyCountDownStarted();
	
	public void notifyCountDownStopped();
	
	public void notifyLoop();
}
