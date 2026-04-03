package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeBendNoteAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-bend";

	public static final String ATTRIBUTE_EFFECT = TGEffectBend.class.getName();

	public TGChangeBendNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGEffectBend effect = (TGEffectBend) context.getAttribute(ATTRIBUTE_EFFECT);

		if ((noteRange != null) && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().setBendNote(note, effect);
			}
		}
	}
}
