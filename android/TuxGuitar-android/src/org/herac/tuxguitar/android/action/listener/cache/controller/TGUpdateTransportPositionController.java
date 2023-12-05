package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.transport.TGTransport;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateTransportPositionController extends TGUpdateItemsController {
	
	public TGUpdateTransportPositionController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		if( midiPlayer.isRunning()) {
			TGTransport.getInstance(context).gotoCaretPosition();
		}
		
		// Call super update.
		super.update(context, actionContext);
	}
}
