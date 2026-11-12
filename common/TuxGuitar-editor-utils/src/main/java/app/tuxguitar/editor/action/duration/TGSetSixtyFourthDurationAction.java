package app.tuxguitar.editor.action.duration;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGSetSixtyFourthDurationAction extends TGSetFixedDurationAction {

	public static final String NAME = "action.note.duration.set-sixty-fourth";

	public static final int VALUE = TGDuration.SIXTY_FOURTH;

	public TGSetSixtyFourthDurationAction(TGContext context) {
		super(context, NAME, VALUE);
	}
}
