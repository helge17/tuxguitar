package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

public class TGDecrementNoteSemitoneAction extends TGTransposeNoteSemitoneAction {

	public static final String NAME = "action.note.general.decrement-semitone";

	public TGDecrementNoteSemitoneAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected boolean canTransposeSemiTone(TGMeasureManager measureManager, TGMeasure measure, TGBeat beat, TGNote note) {
		return measureManager.canMoveSemitoneDown(measure, beat.getStart(), note.getString());
	}
	@Override
	protected boolean transposeSemiTone(TGMeasureManager measureManager, TGMeasure measure, TGBeat beat, TGNote note) {
		return measureManager.moveSemitoneDown(measure, beat.getStart(), note.getString());
	}

}
