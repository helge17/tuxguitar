package org.herac.tuxguitar.player.impl.midiport.vst;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactory;
import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactoryCallback;

public class VSTAudioProcessorFactory implements TGAudioProcessorFactory {
	
	public VSTAudioProcessorFactory() {
		super();
	}
	
	public String getType() {
		return VSTType.VST.toString();
	}
	
	public void createProcessor(TGAudioProcessorFactoryCallback<TGAudioProcessor> callback) {
		try {
			callback.onCreate(new VSTAudioProcessor());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
