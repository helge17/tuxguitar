package app.tuxguitar.editor.action.duration;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGSetWholeDurationAction extends TGSetFixedDurationAction {

	public static final String NAME = "action.note.duration.set-whole";

	public static final int VALUE = TGDuration.WHOLE;

	public TGSetWholeDurationAction(TGContext context) {
		super(context, NAME, VALUE);
	}
}
