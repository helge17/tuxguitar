package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGAddTrackAction extends TGActionBase {
	
	public static final String NAME = "action.track.add";
	
	public TGAddTrackAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		
		getSongManager(context).addTrack(song, track);
	}
}
