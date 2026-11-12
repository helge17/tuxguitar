package app.tuxguitar.editor.action.duration;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGSetEighthDurationAction extends TGSetFixedDurationAction {

	public static final String NAME = "action.note.duration.set-eighth";

	public static final int VALUE = TGDuration.EIGHTH;

	public TGSetEighthDurationAction(TGContext context) {
		super(context, NAME, VALUE);
	}
}
