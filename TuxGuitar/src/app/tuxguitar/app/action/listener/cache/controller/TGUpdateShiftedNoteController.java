package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.util.TGContext;

public class TGUpdateShiftedNoteController extends TGUpdateNoteRangeController {

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
					caret.update();
				}
			});
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
