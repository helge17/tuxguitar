package org.herac.tuxguitar.midi.synth;

public interface TGAudioProcessorFactoryCallback<T extends TGAudioProcessor> {
	
	void onCreate(T processor);
}
