package org.herac.tuxguitar.midi.synth;

public interface TGAbstractProcessorFactory<T extends TGAudioProcessor> {
	
	String getType();
	
	void createProcessor(TGAudioProcessorFactoryCallback<T> callback);
}
