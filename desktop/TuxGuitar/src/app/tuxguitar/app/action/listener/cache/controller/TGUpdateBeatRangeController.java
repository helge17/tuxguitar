package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

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
