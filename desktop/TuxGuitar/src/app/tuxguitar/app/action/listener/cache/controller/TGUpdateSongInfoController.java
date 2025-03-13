package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.util.TGContext;

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
