package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.dialog.marker.TGMarkerList;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedMarkerController extends TGUpdateItemsController {

	public TGUpdateModifiedMarkerController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		TGMarkerList.getInstance(context).fireUpdateProcess();

		TGMarker marker = (TGMarker)actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		if( marker != null ) {
			this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(marker.getMeasure());
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
