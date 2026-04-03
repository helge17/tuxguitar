package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGUpdateRemovedTrackController extends TGUpdateSongController {

	public TGUpdateRemovedTrackController() {
		super();
	}

	@Override
	public void update(final TGContext context, TGActionContext actionContext) {
		if( Boolean.TRUE.equals( actionContext.getAttribute(TGRemoveTrackAction.ATTRIBUTE_SUCCESS)) ) {
			final TGTrack tgTrack = (TGTrack) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

			// clear selection (corresponding track may have been deleted)
			TablatureEditor.getInstance(context).getTablature().getSelector().clearSelection();

			// Update caret position to previous track
			this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
				public void run() {
					Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
					caret.update(tgTrack.getNumber(), caret.getMeasure().getStart(), 1);
				}
			});
		}
		// Call super update.
		super.update(context, actionContext);
	}
}
