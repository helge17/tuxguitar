package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.util.TGContext;

public class TGUpdateSongController extends TGUpdateItemsController {

	public TGUpdateSongController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		this.findUpdateBuffer(context, actionContext).requestUpdateSong();

		super.update(context, actionContext);
	}
}
