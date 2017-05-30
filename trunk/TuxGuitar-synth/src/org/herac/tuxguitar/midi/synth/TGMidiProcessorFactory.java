package org.herac.tuxguitar.midi.synth;

public interface TGMidiProcessorFactory extends TGAbstractProcessorFactory<TGMidiProcessor> {
	
	String getType();
	
	void createProcessor(TGAudioProcessorFactoryCallback<TGMidiProcessor> callback);
}
