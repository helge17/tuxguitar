package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;

/**
 * Makes the channel's takes the source of its sound chain; the effects stay. Only an audio channel has takes (and its
 * source is always its takes), so the choice is refused on any other channel.
 */
public class TGChooseTakesAction extends TGSoundChainAction {

	public static final String NAME = "action.audio.sound-chain.choose-takes";

	public TGChooseTakesAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
		chain.setReceiver(TGSoundChain.source(TGSoundChain.TAKES));
		return channel.isAudioChannel();
	}
}
