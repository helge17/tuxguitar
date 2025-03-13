package app.tuxguitar.player.impl.midiport.vst;

import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.util.TGContext;

public class VSTMidiProcessorFactory implements TGMidiProcessorFactory {

	private TGContext context;

	public VSTMidiProcessorFactory(TGContext context) {
		this.context = context;
	}

	public String getType() {
		return VSTType.VSTI.toString();
	}

	public TGMidiProcessor createProcessor() {
		try {
			return new VSTMidiProcessor(this.context);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
