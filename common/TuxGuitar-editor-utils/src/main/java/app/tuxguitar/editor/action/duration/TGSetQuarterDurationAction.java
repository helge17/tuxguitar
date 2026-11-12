package app.tuxguitar.editor.action.duration;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGSetQuarterDurationAction extends TGSetFixedDurationAction {

	public static final String NAME = "action.note.duration.set-quarter";

	public static final int VALUE = TGDuration.QUARTER;

	public TGSetQuarterDurationAction(TGContext context) {
		super(context, NAME, VALUE);
	}
}
