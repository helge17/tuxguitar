package app.tuxguitar.editor.action.effect;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.util.TGContext;

public class TGChangeVibratoNoteAction extends TGToggleNoteEffectAction {

	public static final String NAME = "action.note.effect.change-vibrato";

	public TGChangeVibratoNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean hasEffect(TGNoteEffect effect) {
		return effect.isVibrato();
	}

	protected void setEffect(TGMeasureManager measureManager, TGNote note, boolean value) {
		measureManager.setVibrato(note, value);
	}
}
