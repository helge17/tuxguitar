package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
