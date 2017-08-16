package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;

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
