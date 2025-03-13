package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

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
