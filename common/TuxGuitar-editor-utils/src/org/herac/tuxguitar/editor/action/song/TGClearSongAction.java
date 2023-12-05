package org.herac.tuxguitar.editor.action.song;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGClearSongAction extends TGActionBase {
	
	public static final String NAME = "action.song.clear";
	
	public static final String ATTRIBUTE_SONG = "songToClear";
	
	public TGClearSongAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = context.getAttribute(ATTRIBUTE_SONG);
		
		songManager.clearSong(song);
	}
}
