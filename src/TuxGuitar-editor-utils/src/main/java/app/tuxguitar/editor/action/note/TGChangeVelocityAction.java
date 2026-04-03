package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeVelocityAction extends TGActionBase {

	public static final String NAME = "action.note.general.velocity";

	public TGChangeVelocityAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Integer velocity = (Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY);

		if (velocity != null) {
			TGMeasureManager measureManager = getSongManager(context).getMeasureManager();
			TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);

			if ((noteRange != null) && !noteRange.isEmpty()) {
				for (TGNote note : noteRange.getNotes()) {
					measureManager.changeVelocity(velocity, note);
				}
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		}
	}
}
