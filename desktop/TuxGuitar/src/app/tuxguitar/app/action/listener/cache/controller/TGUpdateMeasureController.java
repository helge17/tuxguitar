package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

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
