package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedVelocityController extends TGUpdateItemsController {

	public TGUpdateModifiedVelocityController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		if( Boolean.TRUE.equals( actionContext.getAttribute(TGChangeVelocityAction.ATTRIBUTE_SUCCESS)) ) {
			final Integer velocity = (Integer) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY);
			final TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);

			if( header != null ) {
				this.findUpdateBuffer(context).requestUpdateMeasure(header.getNumber());
			}

			this.findUpdateBuffer(context).doPostUpdate(new Runnable() {
				public void run() {
					TGCaret tgCaret = TGSongViewController.getInstance(context).getCaret();
					tgCaret.setVelocity(velocity.intValue());
				}
			});
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
