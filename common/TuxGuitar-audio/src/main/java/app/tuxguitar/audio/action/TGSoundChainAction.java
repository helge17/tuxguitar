package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;

/**
 * Changes the sound chain of a channel (the context's channel). The chain is part of the song: the change is undone like
 * any channel change. A change the chain does not allow is refused and changes nothing.
 */
public abstract class TGSoundChainAction extends TGActionBase {

	protected TGSoundChainAction(TGContext context, String name) {
		super(context, name);
	}

	protected void processAction(TGActionContext context) {
		TGChannel channel = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL);
		TGProgram chain = TGSoundChain.get(channel);
		if (this.change(context, channel, chain)) {
			TGSoundChain.save(channel, chain, getSongManager(context).getFactory());
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}

	// changes the chain of the channel; false when the change is refused
	protected abstract boolean change(TGActionContext context, TGChannel channel, TGProgram chain);
}
