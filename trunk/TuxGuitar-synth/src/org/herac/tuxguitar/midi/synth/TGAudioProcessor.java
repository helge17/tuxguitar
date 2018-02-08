package org.herac.tuxguitar.midi.synth;

import java.util.Map;

public interface TGAudioProcessor {
	
	void close();
	
	void finalize();
	
	boolean isOpen();
	
	boolean isBusy();
	
	void fillBuffer(TGAudioBuffer buffer);
	
	void storeParameters(Map<String, String> parameters);
	
	void restoreParameters(Map<String, String> parameters);
}
