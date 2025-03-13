package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.dialog.marker.TGMarkerList;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedMarkerController extends TGUpdateItemsController {

	public TGUpdateModifiedMarkerController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		TGMarkerList.getInstance(context).fireUpdateProcess();

		// Call super update.
		super.update(context, actionContext);
	}
}
