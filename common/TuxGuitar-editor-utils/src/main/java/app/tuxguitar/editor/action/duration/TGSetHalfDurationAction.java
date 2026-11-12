package app.tuxguitar.editor.action.duration;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGSetHalfDurationAction extends TGSetFixedDurationAction {

	public static final String NAME = "action.note.duration.set-half";

	public static final int VALUE = TGDuration.HALF;

	public TGSetHalfDurationAction(TGContext context) {
		super(context, NAME, VALUE);
	}
}
