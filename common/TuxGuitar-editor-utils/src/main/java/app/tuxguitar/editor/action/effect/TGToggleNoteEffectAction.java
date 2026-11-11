package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

/**
 * Toggles one on/off note effect on the selected notes: they all get it, or all lose it when they all had it.
 */
public abstract class TGToggleNoteEffectAction extends TGActionBase {

	public TGToggleNoteEffectAction(TGContext context, String name) {
		super(context, name);
	}

	protected abstract boolean hasEffect(TGNoteEffect effect);

	protected abstract void setEffect(TGMeasureManager measureManager, TGNote note, boolean value);

	// most effects do not apply to percussion
	protected boolean appliesTo(TGTrack track) {
		return !track.isPercussion();
	}

	protected void processAction(TGActionContext context) {
		toggleSelection(context);
	}

	// false when there was no selection to toggle
	protected boolean toggleSelection(TGActionContext context) {
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if (noteRange == null || noteRange.isEmpty() || !appliesTo(track)) {
			return false;
		}
		boolean newValue = !noteRange.getNotes().stream().allMatch(note -> hasEffect(note.getEffect()));
		TGMeasureManager measureManager = getSongManager(context).getMeasureManager();
		for (TGNote note : noteRange.getNotes()) {
			setEffect(measureManager, note, newValue);
		}
		return true;
	}
}
