package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

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
