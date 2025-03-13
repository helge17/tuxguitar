package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionAdapterManager;
import app.tuxguitar.android.action.listener.cache.TGUpdateBuffer;
import app.tuxguitar.android.action.listener.cache.TGUpdateController;
import app.tuxguitar.util.TGContext;

public class TGUpdateCacheController implements TGUpdateController {

	private boolean updateItems;

	public TGUpdateCacheController(boolean updateItems) {
		this.updateItems = updateItems;
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		this.findUpdateBuffer(context).requestUpdateCache( this.updateItems );
	}

	public TGUpdateBuffer findUpdateBuffer(TGContext context) {
		return TGActionAdapterManager.getInstance(context).getUpdatableActionListener().getBuffer();
	}
}
