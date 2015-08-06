package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateSongInfoController extends TGUpdateItemsController {

	public TGUpdateSongInfoController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGWindow.getInstance(context).loadTitle();
		
		super.update(context, actionContext);
	}
}
