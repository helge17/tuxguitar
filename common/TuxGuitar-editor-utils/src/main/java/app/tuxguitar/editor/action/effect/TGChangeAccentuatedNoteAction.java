package app.tuxguitar.editor.action.effect;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGChangeAccentuatedNoteAction extends TGToggleNoteEffectAction {

	public static final String NAME = "action.note.effect.change-accentuated";

	public TGChangeAccentuatedNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean hasEffect(TGNoteEffect effect) {
		return effect.isAccentuatedNote();
	}

	protected void setEffect(TGMeasureManager measureManager, TGNote note, boolean value) {
		measureManager.setAccentuatedNote(note, value);
	}

	// applies on a percussion track too
	protected boolean appliesTo(TGTrack track) {
		return true;
	}
}
