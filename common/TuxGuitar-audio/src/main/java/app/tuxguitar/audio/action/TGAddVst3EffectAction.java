package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Plugin;

/**
 * Adds a VST3 effect at the end of a channel's sound chain; the same effect may be there more than once. Refused for a
 * VST3 instrument.
 */
public class TGAddVst3EffectAction extends TGSoundChainAction {

	public static final String NAME = "action.audio.sound-chain.add-vst3-effect";

	// the plugin (TGVst3Plugin), as a scan of the VST3 folders found it
	public static final String ATTRIBUTE_PLUGIN = "vst3Plugin";

	private static final String EFFECT_ID = "effect-";

	public TGAddVst3EffectAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
		TGVst3Plugin plugin = context.getAttribute(ATTRIBUTE_PLUGIN);
		if (plugin.isInstrument()) {
			return false;
		}
		chain.addOutput(TGVst3Element.create(newId(chain), plugin));
		return true;
	}

	// "effect-<n>", n one more than the highest among the chain's effects: two effects never share an id
	private static String newId(TGProgram chain) {
		int highest = 0;
		for (int i = 0; i < chain.countOutputs(); i++) {
			String id = chain.getOutput(i).getId();
			if (id.startsWith(EFFECT_ID)) {
				highest = Math.max(highest, Integer.parseInt(id.substring(EFFECT_ID.length())));
			}
		}
		return EFFECT_ID + (highest + 1);
	}
}
