package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import org.herac.tuxguitar.midi.synth.TGAudioProcessorFactoryCallback;
import org.herac.tuxguitar.midi.synth.TGMidiProcessor;
import org.herac.tuxguitar.midi.synth.TGMidiProcessorFactory;
import org.herac.tuxguitar.util.TGContext;

public class VSTMidiProcessorFactory implements TGMidiProcessorFactory {
	
	private TGContext context;
	
	public VSTMidiProcessorFactory(TGContext context) {
		this.context = context;
	}
	
	public String getType() {
		return VSTType.VSTI.toString();
	}
	
	public void createProcessor(TGAudioProcessorFactoryCallback<TGMidiProcessor> callback) {
		try {
			callback.onCreate(new VSTMidiProcessor(this.context));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
