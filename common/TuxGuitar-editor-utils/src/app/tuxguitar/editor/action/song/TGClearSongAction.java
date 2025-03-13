package app.tuxguitar.editor.action.song;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
