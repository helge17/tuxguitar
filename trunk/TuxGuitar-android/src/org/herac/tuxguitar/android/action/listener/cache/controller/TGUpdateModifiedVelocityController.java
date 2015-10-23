package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGChangeVelocityAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGContext;

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
