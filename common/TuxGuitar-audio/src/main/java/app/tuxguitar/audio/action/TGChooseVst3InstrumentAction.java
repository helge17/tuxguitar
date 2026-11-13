package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Plugin;

/**
 * Makes a VST3 instrument the source of a channel's sound chain, in place of its General MIDI program (which the channel
 * keeps); the effects stay. Refused for a VST3 effect, and on an audio channel, whose source is its takes.
 */
public class TGChooseVst3InstrumentAction extends TGSoundChainAction {

	public static final String NAME = "action.audio.sound-chain.choose-vst3-instrument";

	public TGChooseVst3InstrumentAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
		TGVst3Plugin plugin = context.getAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN);
		if (!plugin.isInstrument() || channel.isAudioChannel()) {
			return false;
		}
		chain.setReceiver(TGVst3Element.create("source", plugin));
		return true;
	}
}
