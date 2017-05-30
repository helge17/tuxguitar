package org.herac.tuxguitar.midi.synth;

public interface TGAudioProcessorFactory extends TGAbstractProcessorFactory<TGAudioProcessor> {
	
	String getType();
	
	void createProcessor(TGAudioProcessorFactoryCallback<TGAudioProcessor> callback);
}
