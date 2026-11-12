package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGTakes;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Deletes a take of the track; what was below it plays again. The other takes keep their numbers, and the track's
 * next take number does not go down.
 */
public class TGDeleteTakeAction extends TGActionBase {

	public static final String NAME = "action.audio.take.delete";

	public TGDeleteTakeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		Integer number = context.getAttribute(TGChooseTakeAction.ATTRIBUTE_TAKE);
		TGTake take = TGTakes.find(track, number);
		if (take != null) {
			TGTakes.delete(track, take);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
