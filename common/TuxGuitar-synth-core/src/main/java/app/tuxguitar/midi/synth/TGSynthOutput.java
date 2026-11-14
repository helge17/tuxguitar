package app.tuxguitar.midi.synth;

import app.tuxguitar.util.TGContext;

/**
 * What the TuxGuitar Synthesizer sounds through: an output that drives its blocks, such as an app's live audio device.
 * An app registers its own with the context's synth manager (an extension of this type); a synthesizer opening plays
 * through the first registered one that takes it, else through its own (the desktop's Java Sound line).
 */
public interface TGSynthOutput {

	// whether it plays the synthesizer from now on
	boolean play(TGSynthModel synthesizer);

	// it plays the synthesizer no more
	void stop(TGSynthModel synthesizer);

	// the output the synthesizer plays through from now on: the first registered one that takes it, else its own
	static TGSynthOutput open(TGContext context, TGSynthModel synthesizer, TGSynthOutput own) {
		for (TGSynthOutput output : TGSynthManager.getInstance(context).getExtensions(TGSynthOutput.class)) {
			if (output.play(synthesizer)) {
				return output;
			}
		}
		own.play(synthesizer);
		return own;
	}
}
