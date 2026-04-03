package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

public class TGFixTiedNoteAction extends TGActionBase {

	public static final String NAME = "action.measure.fix-tied-note";

	public TGFixTiedNoteAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext actionContext) {
		TGSongManager songMgr = getSongManager(actionContext);
		TGNote note = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);

		songMgr.getTrackManager().fixInvalidTiedNote(note);
	}

}
