package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGTakes;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Chooses a take of the track to play in the measures it covers among the given ones (comping). Refused when the take
 * covers none of them.
 */
public class TGChooseTakeAction extends TGActionBase {

	public static final String NAME = "action.audio.take.choose";

	public static final String ATTRIBUTE_TAKE = "take";
	public static final String ATTRIBUTE_FIRST_MEASURE = "firstMeasure";
	public static final String ATTRIBUTE_LAST_MEASURE = "lastMeasure";

	public TGChooseTakeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		Integer number = context.getAttribute(ATTRIBUTE_TAKE);
		Integer first = context.getAttribute(ATTRIBUTE_FIRST_MEASURE);
		Integer last = context.getAttribute(ATTRIBUTE_LAST_MEASURE);
		TGTake take = TGTakes.find(track, number);
		if (take != null && TGTakes.choose(track, take, first, last)) {
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
