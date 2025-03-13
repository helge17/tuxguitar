package app.tuxguitar.player.impl.midiport.vst;

import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.util.TGContext;

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
