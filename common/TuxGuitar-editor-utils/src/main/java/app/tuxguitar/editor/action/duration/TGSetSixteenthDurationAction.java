package app.tuxguitar.editor.action.duration;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGSetSixteenthDurationAction extends TGSetFixedDurationAction {

	public static final String NAME = "action.note.duration.set-sixteenth";

	public static final int VALUE = TGDuration.SIXTEENTH;

	public TGSetSixteenthDurationAction(TGContext context) {
		super(context, NAME, VALUE);
	}
}
