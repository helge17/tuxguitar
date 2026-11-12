package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.audio.TGPunchRange;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * Sets the punch range to the measures from the first to the last, by number. Refused when it starts past the end of
 * the song; past the end, it ends with the song.
 */
public class TGSetPunchRangeAction extends TGActionBase {

	public static final String NAME = "action.audio.punch-range.set";

	public static final String ATTRIBUTE_FIRST_MEASURE = "firstMeasure";
	public static final String ATTRIBUTE_LAST_MEASURE = "lastMeasure";

	public TGSetPunchRangeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		Integer first = context.getAttribute(ATTRIBUTE_FIRST_MEASURE);
		Integer last = context.getAttribute(ATTRIBUTE_LAST_MEASURE);
		TGPunchRange range = new TGPunchRange(first, last).endingWith(song);
		if (range != null) {
			TGAudioSession.getInstance(getContext()).setPunchRange(range);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
