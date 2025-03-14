package app.tuxguitar.midi.synth.ui;

import app.tuxguitar.midi.synth.TGAudioProcessor;

public interface TGAudioProcessorUIFactory {

	String getType();

	TGAudioProcessorUI create(TGAudioProcessor processor, TGAudioProcessorUICallback callback);
}
