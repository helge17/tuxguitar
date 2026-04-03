package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeGraceNoteAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-grace";

	public static final String ATTRIBUTE_EFFECT = TGEffectGrace.class.getName();

	public TGChangeGraceNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGEffectGrace effect = (TGEffectGrace) context.getAttribute(TGChangeGraceNoteAction.ATTRIBUTE_EFFECT);

		if ((noteRange != null) && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().setGraceNote(note, effect);
			}
		}
	}
}
