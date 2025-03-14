package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

public class TGUpdatePlayerTracksController extends TGUpdateItemsController {

	public TGUpdatePlayerTracksController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		if( midiPlayer.isRunning()) {
			midiPlayer.updateTracks();
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
