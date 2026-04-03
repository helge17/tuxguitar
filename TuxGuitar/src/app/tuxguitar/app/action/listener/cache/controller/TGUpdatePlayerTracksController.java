package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.util.TGContext;

public class TGUpdatePlayerTracksController extends TGUpdateItemsController {

	public TGUpdatePlayerTracksController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TuxGuitar tuxguitar = TuxGuitar.getInstance();
		if( tuxguitar.getPlayer().isRunning()) {
			tuxguitar.getPlayer().updateTracks();
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
