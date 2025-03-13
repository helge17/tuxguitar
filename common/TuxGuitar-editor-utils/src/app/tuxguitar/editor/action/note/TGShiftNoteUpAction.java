package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

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
