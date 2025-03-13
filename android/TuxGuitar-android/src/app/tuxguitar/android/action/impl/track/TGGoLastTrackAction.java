package app.tuxguitar.android.action.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGGoLastTrackAction extends TGActionBase {

	public static final String NAME = "action.track.go-last";

	public TGGoLastTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGTrack track = getSongManager(context).getLastTrack(song);
		if( track != null ){
			getEditor().getCaret().update(track.getNumber());
		}
	}
}
