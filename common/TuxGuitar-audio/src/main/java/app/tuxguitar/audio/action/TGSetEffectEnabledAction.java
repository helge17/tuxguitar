package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.util.TGContext;

/**
 * Enables or disables an effect of a channel's sound chain: a disabled effect is passed by, and its latency does not
 * count.
 */
public class TGSetEffectEnabledAction extends TGEffectAction {

	public static final String NAME = "action.audio.sound-chain.set-effect-enabled";

	// Boolean
	public static final String ATTRIBUTE_ENABLED = "soundChainEffectEnabled";

	public TGSetEffectEnabledAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGProgram chain, TGProgramElement effect) {
		effect.setEnabled(context.<Boolean>getAttribute(ATTRIBUTE_ENABLED));
		return true;
	}
}
