package org.herac.tuxguitar.editor.action.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGSetTrackLyricsAction extends TGActionBase {
	
	public static final String NAME = "action.track.set-lyric";
	
	public TGSetTrackLyricsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGLyric lyric = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_LYRIC);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		
		track.getLyrics().copyFrom(lyric);
	}
}
