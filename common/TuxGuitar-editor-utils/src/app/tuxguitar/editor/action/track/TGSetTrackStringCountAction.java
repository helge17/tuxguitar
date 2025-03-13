package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGSetTrackStringCountAction extends TGActionBase {

	public static final String NAME = "action.track.set-string-count";

	public static final String ATTRIBUTE_STRING_COUNT = "stringCount";

	public TGSetTrackStringCountAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Integer count = context.getAttribute(ATTRIBUTE_STRING_COUNT);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if( track != null && count != null ){
			getSongManager(context).getTrackManager().changeStringCount(track, count);
		}
	}
}
