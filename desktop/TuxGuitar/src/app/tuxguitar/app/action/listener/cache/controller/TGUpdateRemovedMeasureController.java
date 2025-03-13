package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.Caret;
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
			this.findUpdateBuffer(context, actionContext).requestUpdateSong();

			this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
				public void run() {
					int measureCount = tgSong.countMeasureHeaders();
					Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
					if( caret.getMeasure().getNumber() > measureCount ){
						TGTrack track = tgSongManager.getTrack(tgSong, caret.getTrack().getNumber());
						TGMeasure measure = tgSongManager.getTrackManager().getMeasure(track, measureCount);
						caret.update(track.getNumber(), measure.getStart(), 1);
					}
				}
			});
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
