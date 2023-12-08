package org.herac.tuxguitar.player.impl.jsa.assistant;

public interface SBInstallerlistener {

	public void notifyProcess(String process);
	
	public void notifyFinish();
	
	public void notifyFailed(Throwable throwable);
	
}
