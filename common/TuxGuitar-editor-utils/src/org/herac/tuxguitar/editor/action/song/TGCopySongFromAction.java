package org.herac.tuxguitar.editor.action.song;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGCopySongFromAction extends TGActionBase {
	
	public static final String NAME = "action.song.copy.from";
	
	public static final String ATTRIBUTE_FROM = "from";
	
	public TGCopySongFromAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGSong from = ((TGSong) context.getAttribute(ATTRIBUTE_FROM));
		
		songManager.copySongFrom(song, from);
	}
}
