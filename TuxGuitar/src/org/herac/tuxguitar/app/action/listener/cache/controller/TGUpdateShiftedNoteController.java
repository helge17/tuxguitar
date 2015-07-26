package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.util.TGContext;

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
				this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(header.getNumber());
			}
			
			this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
				public void run() {
					Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
					caret.setStringNumber(tgString.getNumber());
				}
			});
		}
		
		// Call super update.
		super.update(context, actionContext);
	}
}
