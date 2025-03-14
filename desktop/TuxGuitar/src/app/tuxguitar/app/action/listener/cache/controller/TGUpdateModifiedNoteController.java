package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGContext;

public class TGUpdateModifiedNoteController extends TGUpdateMeasureController {

	public TGUpdateModifiedNoteController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		if( Boolean.TRUE.equals( actionContext.getAttribute(TGChangeNoteAction.ATTRIBUTE_SUCCESS)) ) {
			TGBeat beat = (TGBeat) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);

			//reproduzco las notas en el pulso
			TuxGuitar tuxguitar = TuxGuitar.getInstance();
			tuxguitar.playBeat(beat);
		}

		// Call super update.
		super.update(context, actionContext);
	}
}
