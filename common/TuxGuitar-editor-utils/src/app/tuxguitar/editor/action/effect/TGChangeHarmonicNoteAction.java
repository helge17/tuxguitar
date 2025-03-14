package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeHarmonicNoteAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-harmonic";

	public static final String ATTRIBUTE_EFFECT = TGEffectHarmonic.class.getName();

	public TGChangeHarmonicNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGEffectHarmonic harmonic = (TGEffectHarmonic) context.getAttribute(ATTRIBUTE_EFFECT);

		if ((noteRange != null) && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().setHarmonicNote(note, harmonic);
			}
		}
	}
}
