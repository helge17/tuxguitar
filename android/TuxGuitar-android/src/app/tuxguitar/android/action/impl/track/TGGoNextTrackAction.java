package app.tuxguitar.android.action.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGGoNextTrackAction extends TGActionBase {

	public static final String NAME = "action.track.go-next";

	public TGGoNextTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGTrack nextTrack = getSongManager(context).getTrack(song, track.getNumber() + 1);
		if( nextTrack != null ){
			getEditor().getCaret().update(nextTrack.getNumber());
		}
	}
}
