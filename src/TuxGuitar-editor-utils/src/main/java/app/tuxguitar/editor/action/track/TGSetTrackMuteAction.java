package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGSetTrackMuteAction extends TGActionBase {

	public static final String NAME = "action.track.set-mute";

	public static final String ATTRIBUTE_MUTE = "mute";

	public TGSetTrackMuteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Boolean mute = ((Boolean) context.getAttribute(ATTRIBUTE_MUTE));
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGSongManager tgSongManager = getSongManager(context);
		tgSongManager.getTrackManager().changeMute(track, mute);
	}
}
