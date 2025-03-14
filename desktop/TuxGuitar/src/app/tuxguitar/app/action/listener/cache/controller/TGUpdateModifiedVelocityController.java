package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedVelocityController extends TGUpdateNoteRangeController {

	public TGUpdateModifiedVelocityController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		if( Boolean.TRUE.equals( actionContext.getAttribute(TGChangeVelocityAction.ATTRIBUTE_SUCCESS)) ) {
			final Integer velocity = (Integer) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY);
			final TGMeasureHeader header = (TGMeasureHeader) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);

			if( header != null ) {
				this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(header.getNumber());
			}

			this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
				public void run() {
					Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
					caret.setVelocity(velocity.intValue());
				}
			});
		}

		super.update(context, actionContext);
	}
}
