package org.herac.tuxguitar.player.impl.midiport.vst;

import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactoryCallback;
import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;

public class VSTMidiProcessorFactory implements TGMidiProcessorFactory {
	
	public VSTMidiProcessorFactory() {
		super();
	}
	
	public String getType() {
		return VSTType.VSTI.toString();
	}
	
	public void createProcessor(TGAudioProcessorFactoryCallback<TGMidiProcessor> callback) {
		try {
			callback.onCreate(new VSTMidiProcessor());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
