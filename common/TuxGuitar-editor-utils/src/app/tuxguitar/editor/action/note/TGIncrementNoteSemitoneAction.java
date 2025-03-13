package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

public class TGIncrementNoteSemitoneAction extends TGTransposeNoteSemitoneAction {

	public static final String NAME = "action.note.general.increment-semitone";

	public TGIncrementNoteSemitoneAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected boolean canTransposeSemiTone(TGMeasureManager measureManager, TGMeasure measure, TGBeat beat, TGNote note) {
		return measureManager.canMoveSemitoneUp(measure, beat.getStart(), note.getString());
	}
	@Override
	protected boolean transposeSemiTone(TGMeasureManager measureManager, TGMeasure measure, TGBeat beat, TGNote note) {
		return measureManager.moveSemitoneUp(measure, beat.getStart(), note.getString());
	}

}

