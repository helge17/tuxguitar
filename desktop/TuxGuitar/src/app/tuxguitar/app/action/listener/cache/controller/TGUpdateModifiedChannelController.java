package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedChannelController extends TGUpdateItemsController {

	public TGUpdateModifiedChannelController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		MidiPlayer midiPlayer = TuxGuitar.getInstance().getPlayer();
		midiPlayer.updateChannel((TGChannel) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL));

		// Call super update.
		super.update(context, actionContext);
	}
}
