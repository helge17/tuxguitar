package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

public class TGDeleteNoteAction extends TGActionBase {

	public static final String NAME = "action.beat.general.delete-note";

	public TGDeleteNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGNote note = (TGNote) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);

		songManager.getMeasureManager().removeNote(note);
	}
}
