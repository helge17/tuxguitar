package app.tuxguitar.editor.action.effect;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.util.TGContext;

public class TGChangeStaccatoAction extends TGToggleNoteEffectAction {

	public static final String NAME = "action.note.effect.change-staccato";

	public TGChangeStaccatoAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean hasEffect(TGNoteEffect effect) {
		return effect.isStaccato();
	}

	protected void setEffect(TGMeasureManager measureManager, TGNote note, boolean value) {
		measureManager.setStaccato(note, value);
	}
}
