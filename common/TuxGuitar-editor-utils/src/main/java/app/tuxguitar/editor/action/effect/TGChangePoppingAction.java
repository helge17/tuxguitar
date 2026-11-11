package app.tuxguitar.editor.action.effect;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.util.TGContext;

public class TGChangePoppingAction extends TGToggleNoteEffectAction {

	public static final String NAME = "action.note.effect.change-popping";

	public TGChangePoppingAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean hasEffect(TGNoteEffect effect) {
		return effect.isPopping();
	}

	protected void setEffect(TGMeasureManager measureManager, TGNote note, boolean value) {
		measureManager.setPopping(note, value);
	}
}
