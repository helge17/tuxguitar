package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

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
