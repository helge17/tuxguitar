package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGUpdateRemovedMeasureController extends TGUpdateItemsController {

	public TGUpdateRemovedMeasureController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		if( Boolean.TRUE.equals( actionContext.getAttribute(TGRemoveMeasureAction.ATTRIBUTE_SUCCESS)) ) {
			// Update caret position
			final TGSong tgSong = (TGSong) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			final TGSongManager tgSongManager = (TGSongManager) actionContext.getAttribute(TGSongManager.class.getName());

			// Update the song
			this.findUpdateBuffer(context).requestUpdateSong();

			this.findUpdateBuffer(context).doPostUpdate(new Runnable() {
				public void run() {
					int measureCount = tgSong.countMeasureHeaders();
					TGCaret tgCaret = TGSongViewController.getInstance(context).getCaret();
					if( tgCaret.getMeasure().getNumber() > measureCount ){
						TGTrack track = tgSongManager.getTrack(tgSong, tgCaret.getTrack().getNumber());
						TGMeasure measure = tgSongManager.getTrackManager().getMeasure(track, measureCount);
						tgCaret.update(track.getNumber(), measure.getStart(), 1);
					}
				}
			});
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
