package app.tuxguitar.editor.action.song;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
