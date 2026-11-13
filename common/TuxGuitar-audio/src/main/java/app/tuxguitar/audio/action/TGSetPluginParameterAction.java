package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Element;

/**
 * Sets a parameter of a VST3 plugin of a channel's sound chain, by its plain value: of an effect (by its number), or,
 * without one, of the VST3 instrument that is the source. Refused when that is not a VST3 plugin.
 */
public class TGSetPluginParameterAction extends TGSoundChainAction {

	public static final String NAME = "action.audio.sound-chain.set-plugin-parameter";

	// the parameter's id (Integer) and plain value (Double)
	public static final String ATTRIBUTE_PARAMETER = "pluginParameter";
	public static final String ATTRIBUTE_VALUE = "pluginParameterValue";

	public TGSetPluginParameterAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
		Integer effect = context.getAttribute(TGEffectAction.ATTRIBUTE_EFFECT);
		TGProgramElement plugin = (effect != null ? chain.getOutput(effect - 1) : chain.getReceiver());
		if (plugin == null || !TGVst3Element.isVst3(plugin)) {
			return false;
		}
		TGVst3Element.setParameter(plugin, context.<Integer>getAttribute(ATTRIBUTE_PARAMETER), context.<Double>getAttribute(ATTRIBUTE_VALUE));
		return true;
	}
}
