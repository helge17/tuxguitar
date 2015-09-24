package org.herac.tuxguitar.android.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

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
