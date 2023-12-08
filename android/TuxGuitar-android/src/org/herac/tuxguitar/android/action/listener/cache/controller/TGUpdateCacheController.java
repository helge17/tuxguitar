package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionAdapterManager;
import org.herac.tuxguitar.android.action.listener.cache.TGUpdateBuffer;
import org.herac.tuxguitar.android.action.listener.cache.TGUpdateController;
import org.herac.tuxguitar.util.TGContext;

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
