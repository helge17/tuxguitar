package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;

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
