package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateAddedMeasureController extends TGUpdateItemsController {

	public TGUpdateAddedMeasureController() {
		super();
	}
	
	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		final TGSongManager tgSongManager = (TGSongManager) actionContext.getAttribute(TGSongManager.class.getName());
		final TGSong tgSong = (TGSong) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final Integer number = (Integer) actionContext.getAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER);
		
		// Update the created measure
		this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(number);
		
		// Update caret position to new measure
		this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
			public void run() {
				long start = tgSongManager.getMeasureHeader(tgSong, number.intValue()).getStart();
				Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
				caret.update(caret.getTrack().getNumber(), start, caret.getStringNumber());
			}
		});

		// Call super update.
		super.update(context, actionContext);
	}
}
