package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdatePlayerTracksController extends TGUpdateItemsController {
	
	public TGUpdatePlayerTracksController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TuxGuitar tuxguitar = TuxGuitar.getInstance(context);
		if( tuxguitar.getPlayer().isRunning()) {
			tuxguitar.getPlayer().updateTracks();
		}
		
		// Call super update.
		super.update(context, actionContext);
	}
}
