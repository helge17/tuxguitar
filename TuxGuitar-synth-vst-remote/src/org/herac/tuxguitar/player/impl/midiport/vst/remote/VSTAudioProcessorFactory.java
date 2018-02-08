package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import org.herac.tuxguitar.midi.synth.TGAudioProcessor;
import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactory;
import org.herac.tuxguitar.util.TGContext;

public class VSTAudioProcessorFactory implements TGAudioProcessorFactory {
	
	private TGContext context;
	
	public VSTAudioProcessorFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType() {
		return VSTType.VST.toString();
	}
	
	public TGAudioProcessor createProcessor() {
		try {
			return new VSTAudioProcessor(this.context);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
