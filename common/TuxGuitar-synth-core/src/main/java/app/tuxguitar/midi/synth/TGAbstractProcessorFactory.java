package app.tuxguitar.midi.synth;

public interface TGAbstractProcessorFactory<T extends TGAudioProcessor> {

	String getType();

	T createProcessor();
}
