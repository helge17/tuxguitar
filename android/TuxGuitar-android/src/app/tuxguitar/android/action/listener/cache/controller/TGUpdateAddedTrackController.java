package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

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
