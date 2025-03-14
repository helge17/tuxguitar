package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.action.listener.cache.TGUpdateBuffer;
import app.tuxguitar.app.action.listener.cache.TGUpdateContext;
import app.tuxguitar.app.action.listener.cache.TGUpdateController;
import app.tuxguitar.util.TGContext;

public class TGUpdateCacheController implements TGUpdateController {

	private boolean updateItems;

	public TGUpdateCacheController(boolean updateItems) {
		this.updateItems = updateItems;
	}

	public void update(TGContext context, TGActionContext actionContext) {
		this.findUpdateBuffer(context, actionContext).requestUpdateCache( this.updateItems );
	}

	public TGUpdateBuffer findUpdateBuffer(TGContext context, TGActionContext actionContext) {
		return TGUpdateContext.getInstance(context, actionContext).getBuffer();
	}
}
