package app.tuxguitar.editor.action.effect;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.util.TGContext;

public class TGChangeSlideNoteAction extends TGToggleNoteEffectAction {

	public static final String NAME = "action.note.effect.change-slide";

	public TGChangeSlideNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean hasEffect(TGNoteEffect effect) {
		return effect.isSlide();
	}

	protected void setEffect(TGMeasureManager measureManager, TGNote note, boolean value) {
		measureManager.setSlideNote(note, value);
	}
}
