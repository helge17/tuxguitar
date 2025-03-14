package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.transport.TGTransport;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

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
