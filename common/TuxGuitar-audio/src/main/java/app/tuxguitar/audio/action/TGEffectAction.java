package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;

/**
 * Changes one effect of a channel's sound chain, by its number in processing order. Refused when there is no such effect.
 */
public abstract class TGEffectAction extends TGSoundChainAction {

	// the effect's number, from 1 (Integer)
	public static final String ATTRIBUTE_EFFECT = "soundChainEffect";

	protected TGEffectAction(TGContext context, String name) {
		super(context, name);
	}

	protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
		TGProgramElement effect = chain.getOutput(context.<Integer>getAttribute(ATTRIBUTE_EFFECT) - 1);
		return (effect != null && this.change(context, chain, effect));
	}

	// false when the change is refused
	protected abstract boolean change(TGActionContext context, TGProgram chain, TGProgramElement effect);
}
