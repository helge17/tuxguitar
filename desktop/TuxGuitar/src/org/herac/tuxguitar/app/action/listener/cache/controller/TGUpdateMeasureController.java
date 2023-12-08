package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateMeasureController extends TGUpdateItemsController {

	public TGUpdateMeasureController() {
		super();
	}
	
	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		if( header != null ) {
			this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(header.getNumber());
		}
		
		super.update(context, actionContext);
	}
}
