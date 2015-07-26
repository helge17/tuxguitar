package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateItemsOnSuccessController extends TGUpdateItemsController {

	public TGUpdateItemsOnSuccessController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		if( Boolean.TRUE.equals(actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS)) ){
			super.update(context, actionContext);
		}
	}
}
