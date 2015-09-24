package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateTransportPositionController extends TGUpdateItemsController {
	
	public TGUpdateTransportPositionController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TuxGuitar tuxguitar = TuxGuitar.getInstance(context);
		if( tuxguitar.getPlayer().isRunning()) {
			tuxguitar.getTransport().gotoCaretPosition();
		}
		
		// Call super update.
		super.update(context, actionContext);
	}
}
