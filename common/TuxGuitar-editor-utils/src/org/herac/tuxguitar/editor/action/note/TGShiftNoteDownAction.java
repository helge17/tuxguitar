package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

public class TGShiftNoteDownAction extends TGShiftNoteAction {

	public static final String NAME = "action.note.general.shift-down";

	public TGShiftNoteDownAction(TGContext context) {
		super(context, NAME);
		this.sortStringsAscending = false;
	}

	@Override
	protected int shiftNote(TGMeasureManager measureManager, TGNote note) {
		return measureManager.shiftNoteDown(note.getVoice().getBeat().getMeasure(), note.getVoice().getBeat().getStart(), note.getString());
	}
}