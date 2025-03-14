package app.tuxguitar.android.action.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGGoToTrackAction extends TGActionBase {

	public static final String NAME = "action.track.goto";

	public TGGoToTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		if( track != null ){
			getEditor().getCaret().update(track.getNumber());
		}
	}
}
