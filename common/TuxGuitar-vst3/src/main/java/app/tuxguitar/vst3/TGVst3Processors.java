package app.tuxguitar.vst3;

import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.util.TGContext;

/**
 * Gives the TuxGuitar Synthesizer VST3 effects and instruments: their processor factories, on the context's hosting.
 */
public final class TGVst3Processors {

	private TGVst3Processors() {
	}

	// once per context
	public static void install(TGContext context) {
		TGSynthManager manager = TGSynthManager.getInstance(context);
		TGVst3Hosting hosting = TGVst3Hosting.getInstance(context);
		if (manager.findAudioProcessorFactory(TGVst3Element.EFFECT) == null) {
			manager.addExtension(TGAudioProcessorFactory.class, new TGAudioProcessorFactory() {
				public String getType() {
					return TGVst3Element.EFFECT;
				}

				public TGAudioProcessor createProcessor() {
					return new TGVst3EffectProcessor(hosting);
				}
			});
		}
		if (manager.findMidiProcessorFactory(TGVst3Element.INSTRUMENT) == null) {
			manager.addExtension(TGMidiProcessorFactory.class, new TGMidiProcessorFactory() {
				public String getType() {
					return TGVst3Element.INSTRUMENT;
				}

				public TGMidiProcessor createProcessor() {
					return new TGVst3InstrumentProcessor(hosting);
				}
			});
		}
	}
}
