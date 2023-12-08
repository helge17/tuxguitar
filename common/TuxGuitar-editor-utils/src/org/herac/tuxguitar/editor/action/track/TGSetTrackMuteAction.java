package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
