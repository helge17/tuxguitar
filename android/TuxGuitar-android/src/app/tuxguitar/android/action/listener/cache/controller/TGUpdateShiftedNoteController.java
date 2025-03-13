package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.util.TGContext;

public class TGUpdateShiftedNoteController extends TGUpdateItemsController {

	public TGUpdateShiftedNoteController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		if( Boolean.TRUE.equals( actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS)) ) {
			final TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
			final TGString tgString = (TGString) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);

			if( header != null ) {
				this.findUpdateBuffer(context).requestUpdateMeasure(header.getNumber());
			}

			this.findUpdateBuffer(context).doPostUpdate(new Runnable() {
				public void run() {
					TGCaret tgCaret = TGSongViewController.getInstance(context).getCaret();
					tgCaret.setStringNumber(tgString.getNumber());
					tgCaret.update();
				}
			});
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
