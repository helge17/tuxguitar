package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

public class TGUpdateTransportPositionController extends TGUpdateItemsController {

	public TGUpdateTransportPositionController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		if( MidiPlayer.getInstance(context).isRunning() ){
			TGTransport.getInstance(context).gotoCaretPosition();
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
