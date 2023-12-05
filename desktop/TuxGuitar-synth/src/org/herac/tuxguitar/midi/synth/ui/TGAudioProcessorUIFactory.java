package org.herac.tuxguitar.midi.synth.ui;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;

public interface TGAudioProcessorUIFactory {
	
	String getType();
	
	TGAudioProcessorUI create(TGAudioProcessor processor, TGAudioProcessorUICallback callback);
}
