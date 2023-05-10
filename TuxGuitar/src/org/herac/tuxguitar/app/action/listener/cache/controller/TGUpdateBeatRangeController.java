package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateBeatRangeController extends TGUpdateItemsController {

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGBeatRange beats = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);

		for (TGBeat beat : beats.getBeats()) {
			this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(beat.getMeasure().getNumber());
		}
		super.update(context, actionContext);
	}
}
