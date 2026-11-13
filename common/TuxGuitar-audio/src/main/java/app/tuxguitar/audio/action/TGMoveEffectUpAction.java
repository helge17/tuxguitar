package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.util.TGContext;

/**
 * Moves an effect of a channel's sound chain one place earlier in processing order. Refused for the first one.
 */
public class TGMoveEffectUpAction extends TGEffectAction {

	public static final String NAME = "action.audio.sound-chain.move-effect-up";

	public TGMoveEffectUpAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGProgram chain, TGProgramElement effect) {
		if (chain.getOutput(0) == effect) {
			return false;
		}
		chain.moveOutputUp(effect);
		return true;
	}
}
