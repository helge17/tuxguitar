package org.herac.tuxguitar.android.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.transport.TGTransportAdapter;
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
			TGTransportAdapter.getInstance(context).playBeat(beat);
		}
		
		// Call super update.
		super.update(context, actionContext);
	}
}
