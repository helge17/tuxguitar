package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGToggleNoteEnharmonicAction extends TGActionBase {

	public static final String NAME = "action.note.general.toggle-enharmonic";

	public TGToggleNoteEnharmonicAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext tgActionContext) {
		TGNote note = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		TGTrack track = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		if ( (note != null) && ((track==null) || !track.isPercussion()) ) {
			note.toggleAltEnharmonic();
		}
	}


}
