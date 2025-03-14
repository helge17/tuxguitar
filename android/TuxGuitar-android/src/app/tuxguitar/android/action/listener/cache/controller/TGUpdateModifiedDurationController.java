package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedDurationController extends TGUpdateItemsController {

	public TGUpdateModifiedDurationController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		if( header != null ) {
			this.findUpdateBuffer(context).requestUpdateMeasure(header.getNumber());
		}

		this.findUpdateBuffer(context).doPostUpdate(new Runnable() {
			public void run() {
				TGCaret tgCaret = TGSongViewController.getInstance(context).getCaret();
				tgCaret.setChanges(true);
			}
		});

		// Call super update.
		super.update(context, actionContext);
	}
}
