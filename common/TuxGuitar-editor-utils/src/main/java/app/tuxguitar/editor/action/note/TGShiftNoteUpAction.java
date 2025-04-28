package app.tuxguitar.editor.action.note;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

public class TGShiftNoteUpAction extends TGShiftNoteAction {

	public static final String NAME = "action.note.general.shift-up";

	public TGShiftNoteUpAction(TGContext context) {
		super(context, NAME);
		this.sortStringsAscending = true;
	}

	@Override
	protected int shiftNote(TGMeasureManager measureManager, TGNote note) {
		return measureManager.shiftNoteUp(note.getVoice().getBeat().getMeasure(), note.getVoice().getBeat().getStart(), note.getString());
	}
}
