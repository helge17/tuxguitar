package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.impl.GervillProcessorFactory;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;

/**
 * Makes the channel's General MIDI program (its Bank and Program) the source of its sound chain again; the effects stay.
 * Refused on an audio channel.
 */
public class TGChooseGeneralMidiAction extends TGSoundChainAction {

	public static final String NAME = "action.audio.sound-chain.choose-general-midi";

	public TGChooseGeneralMidiAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
		if (channel.isAudioChannel()) {
			return false;
		}
		chain.setReceiver(TGSoundChain.source(GervillProcessorFactory.TYPE));
		return true;
	}
}
