package org.herac.tuxguitar.midi.synth.remote;

public interface TGClientStarter {
	
	String[] createClientCommand(Integer sessionId, Integer serverPort);
}
