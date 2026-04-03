package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeTremoloPickingAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-tremolo-picking";

	public static final String ATTRIBUTE_EFFECT = TGEffectTremoloPicking.class.getName();

	public TGChangeTremoloPickingAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGEffectTremoloPicking effect = ((TGEffectTremoloPicking) context.getAttribute(ATTRIBUTE_EFFECT));

		if ((noteRange != null) && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().setTremoloPicking(note, effect);
			}
		}
	}
}
