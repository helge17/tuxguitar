package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.util.TGContext;

/**
 * Takes an effect out of a channel's sound chain.
 */
public class TGRemoveEffectAction extends TGEffectAction {

	public static final String NAME = "action.audio.sound-chain.remove-effect";

	public TGRemoveEffectAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGProgram chain, TGProgramElement effect) {
		chain.removeOutput(effect);
		return true;
	}
}
