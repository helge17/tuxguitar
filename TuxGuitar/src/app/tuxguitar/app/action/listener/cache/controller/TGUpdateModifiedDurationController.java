package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedDurationController extends TGUpdateBeatRangeController {

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
