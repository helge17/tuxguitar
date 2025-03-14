package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
		this.findUpdateBuffer(context).requestUpdateMeasure(number);

		// Update caret position to new measure
		this.findUpdateBuffer(context).doPostUpdate(new Runnable() {
			public void run() {
				long start = tgSongManager.getMeasureHeader(tgSong, number.intValue()).getStart();
				TGCaret tgCaret = TGSongViewController.getInstance(context).getCaret();
				tgCaret.update(tgCaret.getTrack().getNumber(), start, tgCaret.getStringNumber());
			}
		});

		// Call super update.
		super.update(context, actionContext);
	}
}
