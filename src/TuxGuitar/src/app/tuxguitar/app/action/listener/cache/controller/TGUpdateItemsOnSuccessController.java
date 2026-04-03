package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

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
