package app.tuxguitar.editor.action.duration;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGSetThirtySecondDurationAction extends TGSetFixedDurationAction {

	public static final String NAME = "action.note.duration.set-thirty-second";

	public static final int VALUE = TGDuration.THIRTY_SECOND;

	public TGSetThirtySecondDurationAction(TGContext context) {
		super(context, NAME, VALUE);
	}
}
