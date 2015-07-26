package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.Caret;
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
