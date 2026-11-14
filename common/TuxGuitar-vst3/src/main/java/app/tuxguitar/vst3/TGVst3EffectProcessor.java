package app.tuxguitar.vst3;

/**
 * A VST3 effect in a sound chain: each block of the chain goes through it.
 */
public final class TGVst3EffectProcessor extends TGVst3Processor {

	public TGVst3EffectProcessor(TGVst3Hosting hosting) {
		super(hosting, false);
	}
}
