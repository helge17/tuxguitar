package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateAddedTrackController extends TGUpdateSongController {

	public TGUpdateAddedTrackController() {
		super();
	}
	
	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		final TGTrack tgTrack = (TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		
		// Update caret position to new track
		this.findUpdateBuffer(context).doPostUpdate(new Runnable() {
			public void run() {
				TGCaret tgCaret = TGSongViewController.getInstance(context).getCaret();
				tgCaret.update(tgTrack.getNumber(), tgCaret.getPosition(), 1);
			}
		});
		
		// Call super update.
		super.update(context, actionContext);
	}
}
