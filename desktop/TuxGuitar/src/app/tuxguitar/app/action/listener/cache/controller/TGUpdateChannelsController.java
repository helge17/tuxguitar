package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;

public class TGUpdateChannelsController extends TGUpdateItemsController {

	public TGUpdateChannelsController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		try {
			MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
			midiPlayer.updateChannels();

			// Call super update.
			super.update(context, actionContext);
		} catch (MidiPlayerException e) {
			throw new TGException(e);
		}
	}
}
