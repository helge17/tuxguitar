package org.herac.tuxguitar.player.impl.midiport.vst;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactory;

public class VSTAudioProcessorFactory implements TGAudioProcessorFactory {
	
	public VSTAudioProcessorFactory() {
		super();
	}
	
	public String getType() {
		return VSTType.VST.toString();
	}
	
	public TGAudioProcessor createProcessor() {
		try {
			return new VSTAudioProcessor();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
