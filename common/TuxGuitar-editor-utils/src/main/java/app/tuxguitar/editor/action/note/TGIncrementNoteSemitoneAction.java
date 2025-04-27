package app.tuxguitar.editor.action.note;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

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

