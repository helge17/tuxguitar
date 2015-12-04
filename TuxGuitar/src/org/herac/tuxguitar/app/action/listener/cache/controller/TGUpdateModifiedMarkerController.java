package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.dialog.marker.TGMarkerList;
import org.herac.tuxguitar.util.TGContext;

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
