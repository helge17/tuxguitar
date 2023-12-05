package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateSongController extends TGUpdateItemsController {

	public TGUpdateSongController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		this.findUpdateBuffer(context).requestUpdateSong();
		
		super.update(context, actionContext);
	}
}
