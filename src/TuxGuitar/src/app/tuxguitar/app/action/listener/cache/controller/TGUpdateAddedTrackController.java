package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.Caret;
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
		this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
			public void run() {
				Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
				caret.update(tgTrack.getNumber(), caret.getPosition(), 1);
			}
		});

		// Call super update.
		super.update(context, actionContext);
	}
}
