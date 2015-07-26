package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateModifiedDurationController extends TGUpdateItemsController {

	public TGUpdateModifiedDurationController() {
		super();
	}
	
	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		if( header != null ) {
			this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(header.getNumber());
		}
		
		this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
			public void run() {
				Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
				caret.setChanges(true);
			}
		});
		
		// Call super update.
		super.update(context, actionContext);
	}
}
