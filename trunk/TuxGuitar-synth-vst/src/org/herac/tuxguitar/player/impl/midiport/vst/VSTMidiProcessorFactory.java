package org.herac.tuxguitar.player.impl.midiport.vst;

import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;

public class VSTMidiProcessorFactory implements TGMidiProcessorFactory {
	
	public VSTMidiProcessorFactory() {
		super();
	}
	
	public String getType() {
		return VSTType.VSTI.toString();
	}
	
	public TGMidiProcessor createProcessor() {
		try {
			return new VSTMidiProcessor();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
