package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGUpdateNoteRangeController extends TGUpdateItemsController {

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGNoteRange noteRange = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);

		if( Boolean.TRUE.equals( actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS)) ) {
			TGBeat beat = (TGBeat) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			TuxGuitar tuxguitar = TuxGuitar.getInstance();
			tuxguitar.playBeat(beat);
		}
		if (noteRange!=null && !noteRange.isEmpty()) {
			for (TGMeasure measure : noteRange.getMeasures()) {
				this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(measure.getNumber());
			}
		}
		super.update(context, actionContext);
	}

}
